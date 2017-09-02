package syncjam;

import com.xuggle.xuggler.*;
import com.xuggle.xuggler.io.IURLProtocolHandler;
import com.xuggle.xuggler.io.InputOutputStreamHandler;
import com.xuggle.xuggler.io.XugglerIO;
import syncjam.interfaces.*;
import syncjam.net.NetworkSocket;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to control playing of audio. Thread-safe.
 * Created by Ithmeer on 2/19/2015.
 */
public class ConcurrentAudioController implements AudioController
{
    private volatile SourceDataLine _mLine;

    private volatile FloatControl _volume;

    private volatile BooleanControl _mute;

    private final AtomicInteger _volumeLevel = new AtomicInteger(50);

    private final AtomicBoolean _interrupted = new AtomicBoolean(false);

    private final Playlist _playlist;

    private final PlayController _playController;

    private final CommandQueue _queue;

    // block thread if stopped
    private final Semaphore _pausedSemaphore = new Semaphore(0);

    // synchronized on this
    private boolean _playing;

    // thread-safe container map (synchronized on itself)
    private final Map<SocketAddress, XugglerClient> _clientMap;

    public ConcurrentAudioController(Playlist pl, PlayController playCon, CommandQueue cq)
    {
        _playController = playCon;
        _playlist = pl;
        _queue = cq;

        _clientMap = Collections.synchronizedMap(new HashMap<SocketAddress, XugglerClient>());

        synchronized (this)
        {
            _playing = false;
        }

        Global.setFFmpegLoggingLevel(-1);
    }

    public void addClient(NetworkSocket client)
    {
        synchronized (_clientMap)
        {
            // remove in case this is a reconnect-on-error
            _clientMap.remove(client.getIPAddress());

            try
            {
                _clientMap.put(client.getIPAddress(),
                               new XugglerClient(IContainer.make(), client.getStreamChannel().getOutputStream(),
                                                 client.getIPAddress()));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Play audio and unblock thread.
     */
    @Override
    public synchronized void play()
    {
        if (!_playing)
        {
            _playing = true;
            if (_mLine != null)
                _mLine.start();
            _pausedSemaphore.release();
        }
    }

    /**
     * Stop audio and block thread.
     */
    @Override
    public synchronized void pause()
    {
        if (_playing)
        {
            _playing = false;
            try
            {
                _pausedSemaphore.acquire();
            } catch (InterruptedException e)
            {
                // this should be impossible
                e.printStackTrace();
            }
            if (_mLine != null)
                _mLine.stop();
        }
    }

    /**
     * Set the volume.
     *
     * @param level between 0 and 100
     */
    @Override
    public void setVolume(int level)
    {
        if (level < 0)
            level = 0;
        else if (level > 100)
            level = 100;

        _volumeLevel.set(level);

        if (_mute != null)
        {
            _mute.setValue(level == 0);
        }

        if (_volume != null)
        {
            _volume.setValue(-60 + Math.round(Math.sqrt(level) * 6.0));
        }
    }

    @Override
    public void updateSong()
    {
        // if already playing, does nothing
        play();
        _interrupted.set(true);
    }

    @Override
    public void start()
    {
        while (true)
        {
            try
            {
                Song next = _playlist.getNextSong();
                _playController.setSong(next);
                if (next instanceof DatagramChannelSong)
                {
                    playSong((DatagramChannelSong) next);
                }
                else if (next instanceof BytesSong)
                {
                    playSong((BytesSong) next);
                }
                else if (next instanceof PartialBytesSong)
                {
                    playSong((PartialBytesSong) next);
                }
                else
                {
                    System.out.println("bad format");
                }
            }
            catch (InterruptedException e)
            {
                // quit if interrupted
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Play a song from a socket.
     * @param song the song
     */
    private void playSong(DatagramChannelSong song)
    {
        InputStream channel = song.getStreamChannel();
        playUrl(XugglerIO.map(song.getTitle(), channel), song, false);
    }

    /**
     * Play a song from a byte array.
     * @param song the song
     */
    private void playSong(BytesSong song)
    {
        playUrl(XugglerIO.map(song.getTitle(), new BytesHandler(song.getSongData())), song, true);
    }

    /**
     * Play a song from a byte array.
     * @param song the song
     */
    private void playSong(PartialBytesSong song)
    {
        playUrl(XugglerIO.map(song.getTitle(), new BytesHandler(song.getData())), song, true);
    }

    /**
     * Play a song from a url.
     * @param url the Xuggler url
     * @param song the song
     */
    private void playUrl(String url, Song song, boolean isServer)
    {
        // Create the container for the client/host
        IContainer container = IContainer.make();
        openContainer(container, url, IContainer.Type.READ, null);

        int numStreams = container.getNumStreams();

        int audioStreamId = -1;
        IStreamCoder audioCoder = null;
        for (int i = 0; i < numStreams; i++)
        {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();

            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
            {
                audioStreamId = i;
                audioCoder = coder;
                break;
            }
        }

        if (audioStreamId == -1)
        {
            throw new RuntimeException("could not find audio stream in container: " +
                                       song.getTitle());
        }

        if (audioCoder.open(null, null) < 0)
        {
            throw new RuntimeException("could not open audio decoder for container: " +
                                       song.getTitle());
        }

        openJavaSound(audioCoder);
        container.queryStreamMetaData();

        // container duration in microseconds
        int durationInSecs = (int) (TimeUnit.SECONDS.convert(container.getDuration(),
                                                             TimeUnit.MICROSECONDS));
        if (song.getLength() == 0)
            song.setLength(durationInSecs);

        if (isServer)
        {
            _playController.setSongPosition(0);

            synchronized (_clientMap)
            {
                for (XugglerClient client : _clientMap.values())
                {
                    try
                    {
                        openContainer(container, audioCoder, client);
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        }

        double seekOffset = 0;
        IPacket packet = IPacket.make();

        outer: while (container.readNextPacket(packet) >= 0)
        {
            if (packet.getDuration() == -1)
            {
                break;
            }

            if (packet.getStreamIndex() == audioStreamId)
            {
                // stream to all client containers
                if (isServer)
                {
                    for (XugglerClient client : _clientMap.values())
                    {
                        try
                        {
                            client.getContainer().writePacket(packet);
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                }

                IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());

                int offset = 0;

                while (offset < packet.getSize())
                {
                    int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                    if (bytesDecoded < 0)
                    {
                        System.err.println("decoding error");
                        // something wrong decoding? try the next packet...
                        continue outer;
                    }

                    double calculatedPos = packet.getTimeStamp() * packet.getTimeBase().getDouble();
                    int nextSeekPosition = _playController.getNextSeekPosition();

                    if (isServer)
                    {
                        if (nextSeekPosition != -1)
                        {
                            long timeStamp = (long) (nextSeekPosition / packet.getTimeBase().getDouble());
                            container.seekKeyFrame(audioStreamId, 0, timeStamp, timeStamp, 0);
                        }
                        else
                        {
                            _playController.setSongPosition((int) calculatedPos);
                        }
                    }
                    else
                    {
                        if (nextSeekPosition != -1)
                        {
                            seekOffset = nextSeekPosition - calculatedPos;
                        }

                        _playController.setSongPosition((int) (calculatedPos + seekOffset));
                    }

                    offset += bytesDecoded;
                    if (samples.isComplete())
                    {
                        playJavaSound(samples);
                        if (_interrupted.get())
                            break outer;
                    }
                }
            }
        }

        if (isServer)
        {
            for (XugglerClient client : _clientMap.values())
            {
                try
                {
                    IPacket endPacket = IPacket.make();
                    endPacket.setDuration(-1);
                    client.getContainer().writePacket(endPacket);
                }
                catch (Exception ex)
                {
                }
            }

        }

        _interrupted.set(false);
        closeJavaSound(isServer);
        audioCoder.close();
        container.close();
    }

    public void openContainer(IContainer container, String url, IContainer.Type type,
                              IContainerFormat format)
    {
        int openStatus = container.open(url, type, format);
        if (openStatus < 0)
        {
            IError err = IError.make(openStatus);
            if (err.getType() == IError.Type.ERROR_INTERRUPTED)
                throw new IllegalArgumentException("open song interrupted for: " + url);
            else
                throw new IllegalArgumentException("could not open song: " + url);
        }
    }

    public void openContainer(IContainer orig, IStreamCoder origCoder, XugglerClient client)
    {
        IContainerFormat origFormat = orig.getContainerFormat();
        IContainerFormat format = IContainerFormat.make();
        format.setOutputFormat(origFormat.getInputFormatShortName(), "", "");

        IContainer clientContainer = client.getContainer();

        if (clientContainer.isOpened())
        {
            clientContainer.writeTrailer();
            clientContainer.close();
            clientContainer = IContainer.make();
        }

        IURLProtocolHandler handler = new InputOutputStreamHandler(null, client.channel, false);
        int openStatus = clientContainer.open(handler, IContainer.Type.WRITE,
                                              format, true, false);
        if (openStatus < 0)
        {
            IError err = IError.make(openStatus);
            if (err.getType() == IError.Type.ERROR_INTERRUPTED)
            {
                throw new IllegalArgumentException("open song interrupted for: " + orig.getURL());

            } else
            {
                throw new IllegalArgumentException(String.format(
                        "could not open song: \'%s\' for client %s",
                        orig.getURL(), client.address));
            }
        }

        IStreamCoder coder = IStreamCoder.make(IStreamCoder.Direction.ENCODING,
                                               origCoder.getCodecID());
        coder.setSampleRate(origCoder.getSampleRate());
        coder.setChannels(origCoder.getChannels());
        coder.setTimeBase(origCoder.getTimeBase());

        int coderStatus = coder.open(orig.getMetaData(), IMetaData.make());
        if (coderStatus < 0)
        {
            throw new IllegalArgumentException("open coder failed");
        }
        clientContainer.addNewStream(coder);

        int status = clientContainer.writeHeader();
        if (status < 0)
        {
            throw new IllegalArgumentException("write header failed");
        }
        client.setContainer(clientContainer);
    }

    private void openJavaSound(IStreamCoder aAudioCoder)
    {
        AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
                                                  (int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
                                                  aAudioCoder.getChannels(), true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try
        {
            _mLine = (SourceDataLine) AudioSystem.getLine(info);
            _mLine.open(audioFormat);
            synchronized (this)
            {
                if (_playing)
                {
                    _mLine.start();
                }
            }
            _volume = (FloatControl) _mLine.getControl(FloatControl.Type.MASTER_GAIN);
            _mute = (BooleanControl) _mLine.getControl(BooleanControl.Type.MUTE);
            setVolume(_volumeLevel.get());
        }
        catch (LineUnavailableException e)
        {
            throw new RuntimeException("could not open audio line");
        }
    }

    private void playJavaSound(IAudioSamples aSamples)
    {
        int written;
        int length = aSamples.getSize();
        byte[] rawBytes = aSamples.getData().getByteArray(0, length);

        written = _mLine.write(rawBytes, 0, length);
        while (written != length)
        {
            try
            {
                _pausedSemaphore.acquire();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            _pausedSemaphore.release();

            if (_interrupted.get())
            {
                break;
            }

            written += _mLine.write(rawBytes, written, length - written);
        }
    }

    private void closeJavaSound(boolean drain)
    {
        if (drain)
        {
            _mLine.drain();
        }
        _mLine.close();
    }

    private class BytesHandler implements IURLProtocolHandler
    {
        private final byte[] data;
        private final int dataLength;
        private int index = 0;

        public BytesHandler(byte[] songData)
        {
            data = songData;
            dataLength = data.length;
        }

        @Override
        public int open(String url, int flags)
        {
            index = 0;
            return 0;
        }

        @Override
        public int read(byte[] buf, int size)
        {
            // EOF
            if (index >= dataLength)
                return -1;

            int bytesRead = 0;
            while (index + bytesRead < dataLength && bytesRead < size)
            {
                buf[bytesRead] = data[index + bytesRead];
                bytesRead++;
            }
            index += bytesRead;
            return bytesRead;
        }

        @Override
        public int write(byte[] buf, int size)
        {
            return -1;
        }

        @Override
        public long seek(long offset, int whence)
        {
            int origin = whence;
            if (whence == SEEK_SIZE)
                return dataLength;
            else if (whence == SEEK_SET)
                origin = 0;
            else if (whence == SEEK_END)
                origin = dataLength;
            else if (whence == SEEK_CUR)
                origin = index;

            long delta = origin + offset;
            if (delta < 0)
                index = 0;
            else if (delta > dataLength)
                index = dataLength;
            else
                index = (int) delta;
            return index - whence;
        }

        @Override
        public int close()
        {
            return 0;
        }

        @Override
        public boolean isStreamed(String url, int flags)
        {
            return false;
        }
    }
}