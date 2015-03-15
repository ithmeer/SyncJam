package syncjam;

import com.xuggle.xuggler.*;

import javax.sound.sampled.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ithmeer on 2/19/2015.
 */
public class AudioController
{
    private SourceDataLine mLine;

    private FloatControl volume;

    // block thread if stopped
    private final Semaphore sem = new Semaphore(1);

    private AtomicBoolean playing = new AtomicBoolean(true);

    public AudioController()
    {
    }

    /**
     * Play audio and unblock thread.
     */
    public void play()
    {
        if (!playing.get())
        {
            playing.set(true);
            mLine.start();
            sem.release();
        }
    }

    /**
     * Stop audio and block thread.
     */
    public void stop()
    {
        if (playing.get())
        {
            playing.set(false);
            try
            {
                sem.acquire();
            } catch (InterruptedException e)
            {
                // interrupted manually, just stop line
            }
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

        volume.setValue(-80 + level * 4 / 5.0f);
    }

    public void playSong(String fileName)
    {
        // Create a Xuggler container object
        IContainer container = IContainer.make();
        if (container.open(fileName, IContainer.Type.READ, null) < 0)
            throw new IllegalArgumentException("could not open file: " + fileName);

        //yo
        //System.out.println("Now Playing: " + song.getArtistName() + " - " + song.getSongName());
        //hey

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
            throw new RuntimeException("could not find audio stream in container: " + fileName);

        if (audioCoder.open(null, null) < 0)
            throw new RuntimeException("could not open audio decoder for container: " + fileName);

        openJavaSound(audioCoder);

        IPacket packet = IPacket.make();
        while (container.readNextPacket(packet) >= 0)
        {
            if (packet.getStreamIndex() == audioStreamId)
            {
                IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());

                int offset = 0;

                while (offset < packet.getSize())
                {
                    int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                    if (bytesDecoded < 0)
                        throw new RuntimeException("got error decoding audio in: " + fileName);

                    //NowPlaying.songPosition = (double)packet.getTimeStamp() / ( (double)song_to_play.getSongLength
                    // () / packet.getTimeBase().getDouble() );

                    offset += bytesDecoded;
                    if (samples.isComplete())
                    {
                        playJavaSound(samples);
                    }
                }
            }
        }
        closeJavaSound();

        if (audioCoder != null)
        {
            audioCoder.close();
        }
        if (container != null)
        {
            container.close();
        }
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
            mLine.start();
            volume = (FloatControl) mLine.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(50);
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
            written += mLine.write(rawBytes, written, length - written);
            sem.release();
        }
    }

    private void closeJavaSound()
    {
        if (mLine != null)
        {
            mLine.drain();
            mLine.close();
            mLine = null;
        }
    }
}