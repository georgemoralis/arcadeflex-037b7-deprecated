/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.sound;

import gr.codebb.arcadeflex.WIP.v037b7.sound.samplesH;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstdio.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.mame.common.readsamples;
import static gr.codebb.arcadeflex.old.sound.mixer.*;
import static gr.codebb.arcadeflex.old.sound.mixerH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;

public class samples extends snd_interface {

    public samples() {
        sound_num = SOUND_SAMPLES;
        name = "Samples";
    }

    @Override
    public int chips_num(MachineSound msound) {
        return 0;
    }

    @Override
    public int chips_clock(MachineSound msound) {
        return 0;
    }

    static int firstchannel, numchannels;


    /* Start one of the samples loaded from disk. Note: channel must be in the range */
 /* 0 .. Samplesinterface->channels-1. It is NOT the discrete channel to pass to */
 /* mixer_play_sample() */
    public static void sample_start(int channel, int samplenum, int loop) {
        if (Machine.sample_rate == 0) {
            return;
        }
        if (Machine.samples == null) {
            return;
        }
        if (Machine.samples.sample[samplenum] == null) {
            return;
        }
        if (channel >= numchannels) {
            logerror("error: sample_start() called with channel = %d, but only %d channels allocated\n", channel, numchannels);
            return;
        }
        if (samplenum >= Machine.samples.total) {
            logerror("error: sample_start() called with samplenum = %d, but only %d samples available\n", samplenum, Machine.samples.total);
            return;
        }

        if (Machine.samples.sample[samplenum].resolution == 8) {
            logerror("play 8 bit sample %d, channel %d\n", samplenum, channel);
            mixer_play_sample(firstchannel + channel,
                    new BytePtr(Machine.samples.sample[samplenum].data),
                    Machine.samples.sample[samplenum].length,
                    Machine.samples.sample[samplenum].smpfreq,
                    loop);
        } else {
            logerror("play 16 bit sample %d, channel %d\n", samplenum, channel);
            mixer_play_sample_16(firstchannel + channel,
                    new ShortPtr(Machine.samples.sample[samplenum].data),
                    Machine.samples.sample[samplenum].length,
                    Machine.samples.sample[samplenum].smpfreq,
                    loop);
        }
    }

    public static void sample_set_freq(int channel, int freq) {
        if (Machine.sample_rate == 0) {
            return;
        }
        if (Machine.samples == null) {
            return;
        }
        if (channel >= numchannels) {
            logerror("error: sample_adjust() called with channel = %d, but only %d channels allocated\n", channel, numchannels);
            return;
        }

        mixer_set_sample_frequency(channel + firstchannel, freq);
    }

    public static void sample_set_volume(int channel, int volume) {
        if (Machine.sample_rate == 0) {
            return;
        }
        if (Machine.samples == null) {
            return;
        }
        if (channel >= numchannels) {
            logerror("error: sample_adjust() called with channel = %d, but only %d channels allocated\n", channel, numchannels);
            return;
        }

        mixer_set_volume(channel + firstchannel, volume * 100 / 255);
    }

    public static void sample_stop(int channel) {
        if (Machine.sample_rate == 0) {
            return;
        }
        if (channel >= numchannels) {
            logerror("error: sample_stop() called with channel = %d, but only %d channels allocated\n", channel, numchannels);
            return;
        }

        mixer_stop_sample(channel + firstchannel);
    }

    public static int sample_playing(int channel) {
        if (Machine.sample_rate == 0) {
            return 0;
        }
        if (channel >= numchannels) {
            logerror("error: sample_playing() called with channel = %d, but only %d channels allocated\n", channel, numchannels);
            return 0;
        }

        return mixer_is_sample_playing(channel + firstchannel);
    }

    @Override
    public int start(MachineSound msound) {
        int i;
        int[] vol = new int[MIXER_MAX_CHANNELS];
        samplesH.Samplesinterface intf = (samplesH.Samplesinterface) msound.sound_interface;

        /* read audio samples if available */
        Machine.samples = readsamples(intf.samplenames, Machine.gamedrv.name);

        numchannels = intf.channels;
        for (i = 0; i < numchannels; i++) {
            vol[i] = intf.volume;
        }
        firstchannel = mixer_allocate_channels(numchannels, vol);
        for (i = 0; i < numchannels; i++) {
            String buf = sprintf("Sample #%d", i);
            mixer_set_name(firstchannel + i, buf);
        }
        return 0;
    }

    @Override
    public void stop() {
        //no functionality expected
    }

    @Override
    public void update() {
        //no functionality expected
    }

    @Override
    public void reset() {
        //no functionality expected
    }
}
