package syncjam;

import com.xuggle.xuggler.*;
import com.xuggle.xuggler.io.IURLProtocolHandler;
import com.xuggle.xuggler.io.XugglerIO;

import javax.sound.sampled.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to control playing of audio. Thread-safe.
 * Created by Ithmeer on 2/19/2015.
 */
public class AudioController
{
    private volatile SourceDataLine mLine;

    private volatile FloatControl volume;

    private final AtomicInteger volumeLevel = new AtomicInteger(50);

    private final Playlist playlist;

    // need handle on the thread that drives this class
    private final Thread mainThread;

    // block thread if stopped
    private final Semaphore sem = new Semaphore(0);

    private boolean playing;

    public AudioController(Playlist pl)
    {
        playlist = pl;
        mainThread = Thread.currentThread();
        synchronized (this)
        {
            playing = false;
        }
    }

    /**
     * Play audio and unblock thread.
     */
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

    public void updateSong()
    {
        pause();
        mainThread.interrupt();
    }

    public void start()
    {
        while (true)
        {
            try
            {
                Song next = playlist.getNextSong();
                NowPlaying.setSong(next);
                playSong(next);
            } catch (InterruptedException e)
            {
                // quit if interrupted
                return;
            }
        }
    }

    private void playSong(Song song)
    {
        String url = XugglerIO.map(song.getSongName(), new BytesHandler(song.getSongData()));
        NowPlaying.setSongPosition(0);

        // Create a Xuggler container object
        IContainer container = IContainer.make();
        openContainerSafely(container, url);

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
            throw new RuntimeException("could not find audio stream in container: " + song.getSongName());

        if (audioCoder.open(null, null) < 0)
            throw new RuntimeException("could not open audio decoder for container: " + song.getSongName());

        openJavaSound(audioCoder);

        // container duration in microseconds
        int durationInSecs = (int) (container.getDuration() / 1000000);
        song.setSongLength(durationInSecs);

        IPacket packet = IPacket.make();
outer:  while (container.readNextPacket(packet) >= 0)
        {
            if (packet.getStreamIndex() == audioStreamId)
            {
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
                    int oldPos = NowPlaying.getSongPosition();
                    long timeStamp = (long) (oldPos / packet.getTimeBase().getDouble());
                    if (oldPos > curPos + 1 || oldPos < curPos - 1)
                    {
                        container.seekKeyFrame(audioStreamId, timeStamp, timeStamp, timeStamp, IContainer.SEEK_FLAG_BACKWARDS);
                    }
                    else
                    {
                        NowPlaying.setSongPosition(curPos);
                    }

                    offset += bytesDecoded;
                    if (samples.isComplete())
                    {
                        try
                        {
                            playJavaSound(samples);
                        } catch (InterruptedException e)
                        {
                            // restart playing if interrupted
                            Thread.interrupted();
                            play();
                            break outer;
                        }
                    }
                }
            }
        }

        closeJavaSound();
        audioCoder.close();
        container.close();
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

    private void playJavaSound(IAudioSamples aSamples) throws InterruptedException
    {
        int written;
        int length = aSamples.getSize();
        byte[] rawBytes = aSamples.getData().getByteArray(0, length);

        written = mLine.write(rawBytes, 0, length);
        while (written != length)
        {
            sem.acquire();
            written += mLine.write(rawBytes, written, length - written);
            sem.release();
        }
    }

    private void closeJavaSound()
    {
        mLine.drain();
        mLine.close();
    }

    // open container and retry if interrupted error occurs
    private void openContainerSafely(IContainer container, String url)
    {
        while (true)
        {
            int retVal = container.open(url, IContainer.Type.READ, null);
            if (retVal >= 0)
                break;
            IError err = IError.make(retVal);
            if (err.getType() != IError.Type.ERROR_INTERRUPTED)
                throw new IllegalArgumentException("could not open song: " + url);
        }

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