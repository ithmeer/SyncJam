package syncjam;

import com.xuggle.xuggler.*;

import javax.sound.sampled.*;

/**
 * Created by Ithmeer on 2/19/2015.
 */
public class AudioController
{
    private SourceDataLine mLine;

    /**
     * Start or stop audio.
     */
    public void play(boolean start)
    {
        if (start)
            mLine.start();
        else
            mLine.stop();
    }

    /**
     * Set the volume.
     * @param level between 0 and 100
     */
    public void setVolume(int level)
    {
        FloatControl volume = (FloatControl) mLine.getControl(FloatControl.Type.MASTER_GAIN);
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

                    //NowPlaying.songPosition = (double)packet.getTimeStamp() / ( (double)song_to_play.getSongLength() / packet.getTimeBase().getDouble() );

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
            audioCoder = null;
        }
        if (container != null)
        {
            container.close();
            container = null;
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
            setVolume(75);
        } catch (LineUnavailableException e)
        {
            throw new RuntimeException("could not open audio line");
        }
    }

    private void playJavaSound(IAudioSamples aSamples)
    {
        byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());
        mLine.write(rawBytes, 0, aSamples.getSize());
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