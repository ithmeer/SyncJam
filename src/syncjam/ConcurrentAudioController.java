package syncjam;

import com.xuggle.xuggler.*;
import com.xuggle.xuggler.io.IURLProtocolHandler;
import com.xuggle.xuggler.io.XugglerIO;
import syncjam.interfaces.*;
import syncjam.net.NetworkSocket;
import syncjam.net.client.ClientSideSocket;
import syncjam.net.server.ServerSideSocket;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
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
    private volatile SourceDataLine mLine;

    private volatile FloatControl volume;

    private final AtomicInteger volumeLevel = new AtomicInteger(50);

    private final AtomicBoolean interrupted = new AtomicBoolean(false);

    private final Playlist playlist;

    private final PlayController playController;

    private final CommandQueue queue;

    // effectively final
    private volatile NetworkController _networkController;

    // block thread if stopped
    private final Semaphore sem = new Semaphore(0);

    // synchronized on this
    private boolean playing;

    // thread-safe container map (synchronized on itself)
    private final Map<SocketAddress, XugglerClient> _clientMap;

    public ConcurrentAudioController(Playlist pl, PlayController playCon, CommandQueue cq)
    {
        playController = playCon;
        playlist = pl;
        queue = cq;

        _clientMap = Collections.synchronizedMap(new HashMap<SocketAddress, XugglerClient>());

        synchronized (this)
        {
            playing = false;
        }
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
     * Set the network controller (call before starting)
     * @param networkCon the network controller
     */
    public void setNetworkController(NetworkController networkCon)
    {
        _networkController = networkCon;
    }

    /**
     * Play audio and unblock thread.
     */
    @Override
    public synchronized void play()
    {
        if (!playing)
        {
            playing = true;
            if (mLine != null)
                mLine.start();
            sem.release();
        }
    }

    /**
     * Stop audio and block thread.
     */
    @Override
    public synchronized void pause()
    {
        if (playing)
        {
            playing = false;
            try
            {
                sem.acquire();
            } catch (InterruptedException e)
            {
                // this should be impossible
                e.printStackTrace();
            }
            if (mLine != null)
                mLine.stop();
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

        volumeLevel.set(level);
        if (volume != null)
            volume.setValue(-60 + Math.round(Math.sqrt(level) * 6.0));
    }

    @Override
    public void updateSong()
    {
        // if already playing, does nothing
        play();
        interrupted.set(true);
    }

    @Override
    public void start()
    {
        while (true)
        {
            try
            {
                Song next = playlist.getNextSong();
                playController.setSong(next);
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
        if (!isServer)
        {
            IContainerFormat f = IContainerFormat.make();
            f.setInputFormat("mp3");
            openContainer(container, url, IContainer.Type.READ, f);
        }
        else
        {
            openContainer(container, url, IContainer.Type.READ, null);
        }

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
            playController.setSongPosition(0);

            synchronized (_clientMap)
            {
                List<XugglerClient> deadClients = new LinkedList<XugglerClient>();

                for (XugglerClient client : _clientMap.values())
                {
                    try
                    {
                        openContainer(container, client);
                        IStream c = container.addNewStream(ICodec.ID.CODEC_ID_MP3);
                        IStreamCoder coder = c.getStreamCoder();
                    }
                    catch (Exception ex)
                    {
                        if (client.isDead())
                        {
                            deadClients.add(client);
                        }
                    }
                }

                for (XugglerClient client : deadClients)
                {
                    _clientMap.remove(client.address);
                }
            }
        }

        IPacket packet = IPacket.make();
        outer: while (container.readNextPacket(packet) >= 0)
        {
            if (packet.getStreamIndex() == audioStreamId)
            {
                // stream to all client containers
                if (isServer)
                {
                    List<XugglerClient> deadClients = new LinkedList<XugglerClient>();

                    for (XugglerClient client : _clientMap.values())
                    {
                        try
                        {
                            client.container.writePacket(IPacket.make(packet, true));
                        }
                        catch (Exception ex)
                        {
                            if (client.isDead())
                            {
                                deadClients.add(client);
                            }
                        }
                    }

                    for (XugglerClient client : deadClients)
                    {
                        _clientMap.remove(client.address);
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

                    int curPos = (int) (packet.getTimeStamp() * packet.getTimeBase().getDouble());
                    int oldPos = playController.getSongPosition();
                    long timeStamp = (long) (oldPos / packet.getTimeBase().getDouble());
                    if ((oldPos > curPos + 1 || oldPos < curPos - 1) && isServer)
                    {
                        int length = playController.getSongLength();
                        queue.seek(Math.round((oldPos / (float) length) * 100.0f));
                        mLine.flush();
                        container.seekKeyFrame(audioStreamId, timeStamp, timeStamp, timeStamp,
                                               IContainer.SEEK_FLAG_BACKWARDS);
                    }
                    else
                    {
                        playController.setSongPosition(curPos);
                    }

                    offset += bytesDecoded;
                    if (samples.isComplete())
                    {
                        playJavaSound(samples);
                        if (interrupted.get())
                            break outer;
                    }
                }
            }
        }

        interrupted.set(false);
        closeJavaSound();
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

    public void openContainer(IContainer orig, XugglerClient client)
    {
        IContainerFormat format = orig.getContainerFormat();
        format.setOutputFormat(format.getInputFormatShortName(), "", "");
        int openStatus = client.container.open(client.channel, format);
        if (openStatus < 0)
        {
            IError err = IError.make(openStatus);
            if (err.getType() == IError.Type.ERROR_INTERRUPTED)
            {
                throw new IllegalArgumentException("open song interrupted for: " + orig.getURL());
            }
            else
            {
                throw new IllegalArgumentException(String.format(
                        "could not open song: \'%s\' for client %s",
                        orig.getURL(), client.address));
            }
        }

        client.container.addNewStream(ICodec.ID.CODEC_ID_MP3);
        client.container.writeHeader();
    }

    private void openJavaSound(IStreamCoder aAudioCoder)
    {
        AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
                                                  (int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
                                                  aAudioCoder.getChannels(),
                                                  true,
                                                  false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try
        {
            mLine = (SourceDataLine) AudioSystem.getLine(info);
            mLine.open(audioFormat);
            synchronized (this)
            {
                if (playing)
                {
                    mLine.start();
                }
            }
            volume = (FloatControl) mLine.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(volumeLevel.get());
        } catch (LineUnavailableException e)
        {
            throw new RuntimeException("could not open audio line");
        }
    }

    private void playJavaSound(IAudioSamples aSamples)
    {
        int written;
        int length = aSamples.getSize();
        byte[] rawBytes = aSamples.getData().getByteArray(0, length);

        written = mLine.write(rawBytes, 0, length);
        while (written != length)
        {
            try
            {
                sem.acquire();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            sem.release();
            written += mLine.write(rawBytes, written, length - written);
        }
    }

    private void closeJavaSound()
    {
        mLine.drain();
        mLine.close();
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