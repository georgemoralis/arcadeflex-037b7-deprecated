/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6502.m6502H.*;
import static gr.codebb.arcadeflex.WIP.v037b7.machine._6821pia.*;
import static gr.codebb.arcadeflex.WIP.v037b7.machine._6821piaH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._5220intf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._5220intfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sound.hc55516.*;
import gr.codebb.arcadeflex.common.PtrLib.ShortPtr;
import static gr.codebb.arcadeflex.old.sound.streams.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.*;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.common.libc.cstdlib.rand;

public class exidy {

    public static final int BASE_FREQ = 1789773;
    public static final double BASE_TIME = (1.0 / ((double) BASE_FREQ / 2000000.0));
    public static final double E_CLOCK = (11289000 / 16);
    public static final double CVSD_CLOCK_FREQ = (1000000.0 / 34.0);

    public static final int RIOT_IDLE = 0;
    public static final int RIOT_COUNTUP = 1;
    public static final int RIOT_COUNTDOWN = 2;

    /* 6532 variables */
    static Object riot_timer;
    static /*UINT8*/ int u8_riot_irq_flag;
    static /*UINT8*/ int u8_riot_irq_enable;
    static /*UINT8*/ int u8_riot_porta_data;
    static /*UINT8*/ int u8_riot_porta_ddr;
    static /*UINT8*/ int u8_riot_portb_data;
    static /*UINT8*/ int u8_riot_portb_ddr;
    static /*UINT32*/ int u32_riot_divider;
    static /*UINT8*/ int u8_riot_state;

    /* 6840 variables */
    static /*UINT8*/ int[] u8_sh6840_CR = new int[3];
    static /*UINT8*/ int u8_sh6840_MSB;
    static /*UINT16*/ int[] u16_sh6840_count = new int[3];
    static /*UINT16*/ int[] u16_sh6840_timer = new int[3];
    static /*UINT8*/ int u8_exidy_sfxctrl;

    /* 8253 variables */
    static /*UINT16*/ int[] u16_sh8253_count = new int[3];
    static int[] sh8253_clstate = new int[3];

    /* 5220/CVSD variables */
    static /*UINT8*/ int u8_has_hc55516;
    static /*UINT8*/ int u8_has_tms5220;

    /* sound streaming variables */
    public static class channel_data {

        boolean enable;
        boolean noisy;
        short volume;
        /*UINT32*/
        int u32_step;
        /*UINT32*/
        int u32_fraction;
    };
    static int exidy_stream;
    static double freq_to_step;
    static channel_data[] music_channel = new channel_data[3];
    static channel_data[] sfx_channel = new channel_data[3];

    /*
	 *  PIA callback to generate the interrupt to the main CPU
     */
    public static irqfuncPtr exidy_irq = new irqfuncPtr() {
        public void handler(int state) {
            cpu_set_irq_line(1, 0, state != 0 ? ASSERT_LINE : CLEAR_LINE);
        }
    };

    /**
     * *************************************************************************
     * PIA Interface
	**************************************************************************
     */
    /* PIA 0 */
    static pia6821_interface pia_0_intf = new pia6821_interface(
            /*inputs : A/B,CA/B1,CA/B2 */null, null, null, null, null, null,
            /*outputs: A/B,CA/B2       */ pia_1_portb_w, pia_1_porta_w, pia_1_cb1_w, pia_1_ca1_w,
            /*irqs   : A/B             */ null, null
    );

    /* PIA 1 */
    static pia6821_interface pia_1_intf = new pia6821_interface(
            /*inputs : A/B,CA/B1,CA/B2 */null, null, null, null, null, null,
            /*outputs: A/B,CA/B2       */ pia_0_portb_w, pia_0_porta_w, pia_0_cb1_w, pia_0_ca1_w,
            /*irqs   : A/B             */ null, exidy_irq
    );

    /* Victory PIA 0 */
 /*TODO*///	static pia6821_interface victory_pia_0_intf = new pia6821_interface
/*TODO*///	(
/*TODO*///		/*inputs : A/B,CA/B1,CA/B2 */ null, null, null, null, null, null,
/*TODO*///		/*outputs: A/B,CA/B2       */ null, victory_sound_response_w, victory_sound_irq_clear_w, victory_main_ack_w,
/*TODO*///		/*irqs   : A/B             */ null, exidy_irq
/*TODO*///        );
    /**
     * ************************************************************************
     * Start/Stop Sound
	**************************************************************************
     */
    public static StreamInitPtr exidy_stream_update = new StreamInitPtr() {
        public void handler(int param, ShortPtr buffer, int length) {
            double noise_freq = 0;
            int chan, i;

            /* reset */
            //memset(buffer, 0, length * sizeof(buffer[0]));
            for (int k = 0; k < length * 2; k++) {
                buffer.write(k,(short)0);
            }

            /* if any channels are noisy, generate the noise wave */
            if (sfx_channel[0].noisy || sfx_channel[1].noisy || sfx_channel[2].noisy) {
                /* if noise is clocked by the E, just generate at max frequency */
                if ((u8_exidy_sfxctrl & 1) == 0) {
                    noise_freq = (double) E_CLOCK;
                } /* otherwise, generate noise clocked by channel 1 of the 6840 */ else if (sfx_channel[0].enable) {
                    noise_freq = (u16_sh6840_timer[0]) != 0 ? ((double) BASE_FREQ / (double) u16_sh6840_timer[0] * 0.5) : 0;
                } /* if channel 1 isn't enabled, zap the buffer */ else {
                    noise_freq = 0;
                }
            }

            /* process sfx channels first */
            for (chan = 0; chan < 3; chan++) {
                channel_data c = sfx_channel[chan];

                /* only process enabled channels */
                if (c.enable) {
                    /* special case channel 0: sfxctl controls its output */
                    if (chan == 0 && (u8_exidy_sfxctrl & 2) != 0) {
                        c.u32_fraction += length * c.u32_step;
                    } /* otherwise, generate normally: non-noisy case first */ else if (!c.noisy) {
                        /*UINT32*/
                        int frac = c.u32_fraction, step = c.u32_step;
                        short vol = c.volume;
                        for (i = 0; i < length; i++) {
                            if ((frac & 0x1000000) != 0) {
                                buffer.write(i, (short) (buffer.read(i) + vol));
                            }
                            frac += step;
                        }
                        c.u32_fraction = frac;
                    } /* noisy case - determine the effective noise step */ else {
                        /*
						Explanation of noise
	
						The noise source can be clocked by 1 of 2 sources, depending on sfxctrl bit 0
	
							(sfxctrl & 1) == 0	-. clock = E
							(sfxctrl & 1) != 0	-. clock = 6840 channel 0
	
						The noise source then becomes the clock for any channels using the external
						clock. On average, the frequency of the clock for that channel should be
						1/4 of the noise frequency, with a random variance on each step. The external
						clock still causes the timer to count, so we must further divide by the
						timer's count value in order to determine the final frequency. To simulate
						the variance, we compute the effective step value, and then apply a random
						offset to it after each sample is generated
                         */
 /*UINT32*/
                        int avgstep = (u16_sh6840_timer[chan]) != 0 ? (int) (freq_to_step * (noise_freq * 0.25) / (double) u16_sh6840_timer[chan]) : 0;
                        /*UINT32*/
                        int frac = c.u32_fraction;
                        short vol = c.volume;

                        avgstep /= 32768;
                        for (i = 0; i < length; i++) {
                            if ((frac & 0x1000000) != 0) {
                                buffer.write(i, (short) (buffer.read(i) + vol));
                            }
                            /* add two random values to get a distribution that is weighted toward the middle */
                            frac += ((rand() & 32767) + (rand() & 32767)) * avgstep;
                        }
                        c.u32_fraction = frac;
                    }
                }
            }

            /* process music channels second */
            for (chan = 0; chan < 3; chan++) {
                channel_data c = music_channel[chan];

                /* only process enabled channels */
                if (c.enable) {
                    /*UINT32*/
                    int step = c.u32_step;
                    /*UINT32*/
                    int frac = c.u32_fraction;

                    for (i = 0; i < length; i++) {
                        if ((frac & 0x0800000) != 0) {
                            buffer.write(i, (short) (buffer.read(i) + c.volume));
                        }
                        frac += step;
                    }
                    c.u32_fraction = frac;
                }
            }
        }
    };

    static int common_start() {
        int i;

        /* determine which sound hardware is installed */
        u8_has_hc55516 = 0;
        u8_has_tms5220 = 0;
        for (i = 0; i < MAX_SOUND; i++) {
            if (Machine.drv.sound[i].sound_type == SOUND_TMS5220) {
                u8_has_tms5220 = 1;
            }
            if (Machine.drv.sound[i].sound_type == SOUND_HC55516) {
                u8_has_hc55516 = 1;
            }
        }

        /* allocate the stream */
        exidy_stream = stream_init("Exidy custom", 100, Machine.sample_rate, 0, exidy_stream_update);

        /* compute the frequency-to-step conversion factor */
        if (Machine.sample_rate != 0) {
            freq_to_step = (double) (1 << 24) / (double) Machine.sample_rate;
        } else {
            freq_to_step = 0.0;
        }

        /* initialize the sound channels */
        //memset(music_channel, 0, sizeof(music_channel));
        //memset(sfx_channel, 0, sizeof(sfx_channel));
        for (int k = 0; k < 3; k++) {
            music_channel[k] = new channel_data();
            sfx_channel[k] = new channel_data();
        }
        music_channel[0].volume = music_channel[1].volume = music_channel[2].volume = 32767 / 6;
        music_channel[0].u32_step = music_channel[1].u32_step = music_channel[2].u32_step = 0;
        sfx_channel[0].u32_step = sfx_channel[1].u32_step = sfx_channel[2].u32_step = 0;

        /* Init PIA */
        pia_reset();

        /* Init 6532 */
        riot_timer = null;
        u8_riot_irq_flag = 0;
        u8_riot_irq_enable = 0;
        u8_riot_porta_data = 0xff;
        u8_riot_portb_data = 0xff;
        u32_riot_divider = 1;
        u8_riot_state = RIOT_IDLE;

        /* Init 6840 */
        u8_sh6840_MSB = 0;
        u8_sh6840_CR[0] = u8_sh6840_CR[1] = u8_sh6840_CR[2] = 0;
        u16_sh6840_timer[0] = u16_sh6840_timer[1] = u16_sh6840_timer[2] = 0;
        u8_exidy_sfxctrl = 0;

        /* Init 8253 */
        u16_sh8253_count[0] = u16_sh8253_count[1] = u16_sh8253_count[2] = 0;
        sh8253_clstate[0] = sh8253_clstate[1] = sh8253_clstate[2] = 0;

        return 0;
    }

    public static ShStartPtr exidy_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            pia_unconfig();
            /* Init PIA */
            pia_config(0, PIA_STANDARD_ORDERING, pia_0_intf);
            pia_config(1, PIA_STANDARD_ORDERING, pia_1_intf);
            return common_start();
        }
    };

    /*TODO*///	public static ShStartPtr victory_sh_start = new ShStartPtr() { public int handler(MachineSound msound) 
/*TODO*///	{
/*TODO*///		/* Init PIA */
/*TODO*///		pia_config(0, PIA_STANDARD_ORDERING, victory_pia_0_intf);
/*TODO*///		pia_0_cb1_w.handler(0, 1);
/*TODO*///		return common_start();
/*TODO*///	} };
    /**
     * ************************************************************************
     * 6532 RIOT
	**************************************************************************
     */
    public static timer_callback riot_interrupt = new timer_callback() {
        public void handler(int param) {
            if (u8_riot_state == RIOT_COUNTUP) {
                u8_riot_irq_flag |= 0x80;
                /* set timer interrupt flag */
                if (u8_riot_irq_enable != 0) {
                    cpu_set_irq_line(1, M6502_INT_IRQ, ASSERT_LINE);
                }
                u8_riot_state = RIOT_COUNTDOWN;
                riot_timer = timer_set(TIME_IN_USEC(BASE_TIME * 0xFF), 0, riot_interrupt);
            } else {
                riot_timer = null;
                u8_riot_state = RIOT_IDLE;
            }
        }
    };

    public static WriteHandlerPtr exidy_shriot_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            offset &= 0x7f;
            switch (offset) {
                case 0:
                    /* port A */
                    if (u8_has_hc55516 != 0) {
                        cpu_set_reset_line(2, (data & 0x10) != 0 ? CLEAR_LINE : ASSERT_LINE);
                    }
                    u8_riot_porta_data = ((u8_riot_porta_data & ~u8_riot_porta_ddr) | (data & u8_riot_porta_ddr)) & 0xFF;
                    return;

                case 1:
                    /* port A DDR */
                    u8_riot_porta_ddr = data & 0xFF;
                    break;

                case 2:
                    /* port B */
                    if (u8_has_tms5220 != 0) {
                        if ((data & 0x01) == 0 && (u8_riot_portb_data & 0x01) != 0) {
                            u8_riot_porta_data = tms5220_status_r.handler(0) & 0xFF;
                            logerror("(%f)%04X:TMS5220 status read = %02X\n", timer_get_time(), cpu_getpreviouspc(), u8_riot_porta_data);
                        }
                        if ((data & 0x02) != 0 && (u8_riot_portb_data & 0x02) == 0) {
                            logerror("(%f)%04X:TMS5220 data write = %02X\n", timer_get_time(), cpu_getpreviouspc(), u8_riot_porta_data);
                            tms5220_data_w.handler(0, u8_riot_porta_data);
                        }
                    }
                    u8_riot_portb_data = ((u8_riot_portb_data & ~u8_riot_portb_ddr) | (data & u8_riot_portb_ddr)) & 0xFF;
                    return;

                case 3:
                    /* port B DDR */
                    u8_riot_portb_ddr = data & 0xFF;
                    break;

                case 7:
                    /* 0x87 - Enable Interrupt on PA7 Transitions */
                    return;

                case 0x14:
                case 0x1c:
                    cpu_set_irq_line(1, M6502_INT_IRQ, CLEAR_LINE);
                    u8_riot_irq_enable = offset & 0x08;
                    u32_riot_divider = 1;
                    if (riot_timer != null) {
                        timer_remove(riot_timer);
                    }
                    riot_timer = timer_set(TIME_IN_USEC((u32_riot_divider * BASE_TIME) * data), 0, riot_interrupt);
                    u8_riot_state = RIOT_COUNTUP;
                    return;

                case 0x15:
                case 0x1d:
                    cpu_set_irq_line(1, M6502_INT_IRQ, CLEAR_LINE);
                    u8_riot_irq_enable = offset & 0x08;
                    u32_riot_divider = 8;
                    if (riot_timer != null) {
                        timer_remove(riot_timer);
                    }
                    riot_timer = timer_set(TIME_IN_USEC((u32_riot_divider * BASE_TIME) * data), 0, riot_interrupt);
                    u8_riot_state = RIOT_COUNTUP;
                    return;

                case 0x16:
                case 0x1e:
                    cpu_set_irq_line(1, M6502_INT_IRQ, CLEAR_LINE);
                    u8_riot_irq_enable = offset & 0x08;
                    u32_riot_divider = 64;
                    if (riot_timer != null) {
                        timer_remove(riot_timer);
                    }
                    riot_timer = timer_set(TIME_IN_USEC((u32_riot_divider * BASE_TIME) * data), 0, riot_interrupt);
                    u8_riot_state = RIOT_COUNTUP;
                    return;

                case 0x17:
                case 0x1f:
                    cpu_set_irq_line(1, M6502_INT_IRQ, CLEAR_LINE);
                    u8_riot_irq_enable = offset & 0x08;
                    u32_riot_divider = 1024;
                    if (riot_timer != null) {
                        timer_remove(riot_timer);
                    }
                    riot_timer = timer_set(TIME_IN_USEC((u32_riot_divider * BASE_TIME) * data), 0, riot_interrupt);
                    u8_riot_state = RIOT_COUNTUP;
                    return;

                default:
                    logerror("Undeclared RIOT write: %x=%x\n", offset, data);
                    return;
            }
            return;
            /* will never execute this */
        }
    };

    static int temp;
    public static ReadHandlerPtr exidy_shriot_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            offset &= 7;
            switch (offset) {
                case 0x00:
                    return u8_riot_porta_data & 0xFF;

                case 0x01:
                    /* port A DDR */
                    return u8_riot_porta_ddr & 0xFF;

                case 0x02:
                    if (u8_has_tms5220 != 0) {
                        u8_riot_portb_data &= ~0x0c;
                        if (tms5220_ready_r() == 0) {
                            u8_riot_portb_data |= 0x04;
                        }
                        if (tms5220_int_r() == 0) {
                            u8_riot_portb_data |= 0x08;
                        }
                    }
                    return u8_riot_portb_data & 0xFF;

                case 0x03:
                    /* port B DDR */
                    return u8_riot_portb_ddr & 0xFF;

                case 0x05:
                /* 0x85 - Read Interrupt Flag Register */
                case 0x07:
                    temp = u8_riot_irq_flag;
                    u8_riot_irq_flag = 0;
                    /* Clear int flags */
                    cpu_set_irq_line(1, M6502_INT_IRQ, CLEAR_LINE);
                    return temp;

                case 0x04:
                case 0x06:
                    u8_riot_irq_flag = 0;
                    cpu_set_irq_line(1, M6502_INT_IRQ, CLEAR_LINE);
                    if (u8_riot_state == RIOT_COUNTUP) {
                        return (int) (timer_timeelapsed(riot_timer) / TIME_IN_USEC(u32_riot_divider * BASE_TIME));
                    } else {
                        return (int) (timer_timeleft(riot_timer) / TIME_IN_USEC(u32_riot_divider * BASE_TIME));
                    }

                default:
                    logerror("Undeclared RIOT read: %x  PC:%x\n", offset, cpu_get_pc());
                    return 0xff;
            }
            //return 0;
        }
    };

    /**
     * ************************************************************************
     * 8253 Timer
	**************************************************************************
     */
    public static WriteHandlerPtr exidy_sh8253_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int chan;

            stream_update(exidy_stream, 0);

            offset &= 3;
            switch (offset) {
                case 0:
                case 1:
                case 2:
                    chan = offset;
                    if (sh8253_clstate[chan] == 0) {
                        sh8253_clstate[chan] = 1;
                        u16_sh8253_count[chan] = (u16_sh8253_count[chan] & 0xff00) | (data & 0x00ff);
                    } else {
                        sh8253_clstate[chan] = 0;
                        u16_sh8253_count[chan] = (u16_sh8253_count[chan] & 0x00ff) | ((data << 8) & 0xff00);
                        if (u16_sh8253_count[chan] != 0) {
                            music_channel[chan].u32_step = (int) (freq_to_step * (double) BASE_FREQ / (double) u16_sh8253_count[chan]);
                        } else {
                            music_channel[chan].u32_step = 0;
                        }
                    }
                    break;

                case 3:
                    chan = (data & 0xc0) >> 6;
                    music_channel[chan].enable = ((data & 0x0e) != 0);
                    break;
            }
        }
    };

    public static ReadHandlerPtr exidy_sh8253_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            logerror("8253(R): %x\n", offset);
            return 0;
        }
    };

    /**
     * ************************************************************************
     * 6840 Timer
	**************************************************************************
     */
    public static ReadHandlerPtr exidy_sh6840_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            logerror("6840R %x\n", offset);
            return 0;
        }
    };

    public static WriteHandlerPtr exidy_sh6840_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int ch;

            stream_update(exidy_stream, 0);

            offset &= 7;
            switch (offset) {
                case 0:
                    if ((u8_sh6840_CR[1] & 0x01) != 0) {
                        u8_sh6840_CR[0] = data & 0xFF;
                    } else {
                        u8_sh6840_CR[2] = data & 0xFF;
                    }
                    break;

                case 1:
                    u8_sh6840_CR[1] = data & 0xFF;
                    break;

                case 2:
                case 4:
                case 6:
                    u8_sh6840_MSB = data & 0xFF;
                    break;

                case 3:
                case 5:
                case 7:
                    ch = (offset - 3) / 2;
                    u16_sh6840_count[ch] = u16_sh6840_timer[ch] = (u8_sh6840_MSB << 8) | (data & 0xff);
                    if (u16_sh6840_timer[ch] != 0) {
                        sfx_channel[ch].u32_step = (int) (freq_to_step * (double) BASE_FREQ / (double) u16_sh6840_timer[ch]);
                    } else {
                        sfx_channel[ch].u32_step = 0;
                    }
                    break;
            }

            sfx_channel[0].enable = ((u8_sh6840_CR[0] & 0x80) != 0 && u16_sh6840_timer[0] != 0);
            sfx_channel[1].enable = ((u8_sh6840_CR[1] & 0x80) != 0 && u16_sh6840_timer[1] != 0);
            sfx_channel[2].enable = ((u8_sh6840_CR[2] & 0x80) != 0 && u16_sh6840_timer[2] != 0);

            sfx_channel[0].noisy = ((u8_sh6840_CR[0] & 0x02) == 0);
            sfx_channel[1].noisy = ((u8_sh6840_CR[1] & 0x02) == 0);
            sfx_channel[2].noisy = ((u8_sh6840_CR[2] & 0x02) == 0);
        }
    };

    /**
     * ************************************************************************
     * Special Sound FX Control
	**************************************************************************
     */
    public static WriteHandlerPtr exidy_sfxctrl_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            stream_update(exidy_stream, 0);

            offset &= 3;
            switch (offset) {
                case 0:
                    u8_exidy_sfxctrl = data & 0xFF;
                    break;

                case 1:
                case 2:
                case 3:
                    sfx_channel[offset - 1].volume = (short) (((data & 7) * (32767 / 6)) / 7);
                    break;
            }
        }
    };

    /**
     * ************************************************************************
     * Mousetrap Digital Sound
	**************************************************************************
     */
    public static WriteHandlerPtr mtrap_voiceio_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((offset & 0x10) == 0) {
                hc55516_digit_clock_clear_w(0, data);
                hc55516_clock_set_w(0, data);
            }
            if ((offset & 0x20) == 0) {
                u8_riot_portb_data = data & 1;
            }
        }
    };

    public static ReadHandlerPtr mtrap_voiceio_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            if ((offset & 0x80) == 0) {
                int data = (u8_riot_porta_data & 0x06) >> 1;
                data |= (u8_riot_porta_data & 0x01) << 2;
                data |= (u8_riot_porta_data & 0x08);
                return data;
            }
            if ((offset & 0x40) == 0) {
                int clock_pulse = (int) (timer_get_time() * (2.0 * CVSD_CLOCK_FREQ));
                return (clock_pulse & 1) << 7;
            }
            return 0;
        }
    };
}
