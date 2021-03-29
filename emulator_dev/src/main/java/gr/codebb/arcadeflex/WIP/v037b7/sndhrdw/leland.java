/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

import gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i186;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i186.i86_set_irq_callback;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.i186.i86_set_irq_line;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_CPU3;
import gr.codebb.arcadeflex.common.PtrLib.ShortPtr;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import gr.codebb.arcadeflex.old.sound.streams.StreamInitPtr;
import static gr.codebb.arcadeflex.old.sound.streams.stream_init;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.install_mem_read_handler;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.install_mem_write_handler;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.install_port_read_handler;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memory.install_port_write_handler;
import gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.IOReadPort;
import gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.IOWritePort;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MRA_BANK6;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MRA_BANK7;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MRA_RAM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MRA_ROM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MWA_BANK6;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MWA_BANK7;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MWA_RAM;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MWA_ROM;
import gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MemoryReadAddress;
import gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.MemoryWriteAddress;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.cpu_setbank;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.sndintrf.sound_reset;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf.YM2151_data_port_0_w;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf.YM2151_register_port_0_w;
import static gr.codebb.arcadeflex.WIP.v037b7.sound._2151intf.YM2151_status_port_0_r;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.sound.streams.stream_update;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ReadHandlerPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ShStartPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ShStopPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.MAX_SOUND;
import gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;
import static gr.codebb.arcadeflex.v037b7.mame.sndintrfH.SOUND_YM2151;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;

public class leland {

    /**
     * ***********************************
     *
     * 1st generation sound
     *
     ************************************
     */
    public static final int DAC_BUFFER_SIZE = 1024;
    public static final int DAC_BUFFER_MASK = (DAC_BUFFER_SIZE - 1);

    static UBytePtr[] dac_buffer = new UBytePtr[2];
    static /*UINT32*/ int[] dac_bufin = new int[2];
    static /*UINT32*/ int[] dac_bufout = new int[2];

    static int dac_stream;

    public static StreamInitPtr leland_update
            = new StreamInitPtr() {
        public void handler(int ch, ShortPtr buffer, int length) {
            int dacnum;

            /* reset the buffer */
            memset(buffer, 0, length * 2);
            for (dacnum = 0; dacnum < 2; dacnum++) {
                int bufout = dac_bufout[dacnum];
                int count = (dac_bufin[dacnum] - bufout) & DAC_BUFFER_MASK;

                if (count > 300) {
                    UBytePtr base = dac_buffer[dacnum];
                    int i;

                    for (i = 0; i < length && count > 0; i++, count--) {
                        //buffer[i] += ((INT16)base[bufout] - 0x80) * 0x40;
                        buffer.write(i, (short) (buffer.read(i) + ((short) base.read(bufout) - 0x80) * 0x40));
                        bufout = (bufout + 1) & DAC_BUFFER_MASK;
                    }
                    dac_bufout[dacnum] = bufout;
                }
            }
        }
    };
    public static ShStartPtr leland_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            /* reset globals */
            dac_buffer[0] = null;
            dac_buffer[1] = null;
            dac_bufin[0] = dac_bufin[1] = 0;
            dac_bufout[0] = dac_bufout[1] = 0;

            /* skip if no sound */
            if (Machine.sample_rate == 0) {
                return 0;
            }

            /* allocate the stream */
            dac_stream = stream_init("Onboard DACs", 50, 256 * 60, 0, leland_update);

            /* allocate memory */
            dac_buffer[0] = new UBytePtr(DAC_BUFFER_SIZE);
            dac_buffer[1] = new UBytePtr(DAC_BUFFER_SIZE);
            if (dac_buffer[0] == null || dac_buffer[1] == null) {
                dac_buffer[0] = null;
                dac_buffer[1] = null;
                return 1;
            }

            return 0;
        }
    };

    public static ShStopPtr leland_sh_stop = new ShStopPtr() {
        public void handler() {
            if (dac_buffer[0] == null || dac_buffer[1] == null) {
                dac_buffer[0] = null;
                dac_buffer[1] = null;
            }
        }
    };

    public static void leland_dac_update(int dacnum, UBytePtr base) {
        UBytePtr buffer = dac_buffer[dacnum];
        int bufin = dac_bufin[dacnum];
        int row;

        /* skip if nothing */
        if (buffer == null) {
            return;
        }

        /* copy data from VRAM */
        for (row = 0; row < 256; row++) {
            buffer.write(bufin, base.read(row * 0x80));
            bufin = (bufin + 1) & DAC_BUFFER_MASK;
        }

        /* update the buffer */
        dac_bufin[dacnum] = bufin;
    }

    /**
     * ***********************************
     *
     * 2nd-4th generation sound
     *
     ************************************
     */
    public static final int LOG_INTERRUPTS = 0;
    public static final int LOG_DMA = 0;
    public static final int LOG_SHORTAGES = 0;
    public static final int LOG_TIMER = 0;
    public static final int LOG_COMM = 0;
    public static final int LOG_PORTS = 0;
    public static final int LOG_DAC = 0;
    public static final int LOG_EXTERN = 0;
    public static final int LOG_PIT = 0;
    public static final int LOG_OPTIMIZATION = 0;

    /* according to the Intel manual, external interrupts are not latched */
 /* however, I cannot get this system to work without latching them */
    public static final int LATCH_INTS = 1;
    public static final int DAC_VOLUME_SCALE = 4;
    public static final int CPU_RESUME_TRIGGER = 7123;

    static int dma_stream;
    static int nondma_stream;
    static int extern_stream;

    static UBytePtr ram_base = new UBytePtr();
    static int/*UINT8*/ has_ym2151;
    static int/*UINT8*/ is_redline;

    static int/*UINT8*/ last_control;
    static int/*UINT8*/ clock_active;
    static int/*UINT8*/ clock_tick;

    static int[]/*UINT8*/ u8_sound_command = new int[2];
    static int/*UINT8*/ sound_response;

    static int/*UINT32*/ ext_start;
    static int/*UINT32*/ ext_stop;
    static int/*UINT8*/ ext_active;
    static UBytePtr ext_base;

    public static UBytePtr active_mask;
    static int total_reads;

    static class mem_state {

        char lower;
        char upper;
        char middle;
        char middle_size;
        char peripheral;
    };

    static class timer_state {

        char control;
        char maxA;
        char maxB;
        char count;
        Object int_timer;
        Object time_timer;
        double last_time;

        public static timer_state[] create(int n) {
            timer_state[] a = new timer_state[n];
            for (int k = 0; k < n; k++) {
                a[k] = new timer_state();
            }
            return a;
        }
    };

    static class dma_state {

        int/*UINT32*/ source;
        int/*UINT32*/ dest;
        char count;
        char control;
        int/*UINT8*/ u8_finished;
        Object finish_timer;

        public static dma_state[] create(int n) {
            dma_state[] a = new dma_state[n];
            for (int k = 0; k < n; k++) {
                a[k] = new dma_state();
            }
            return a;
        }
    };

    static class intr_state {

        int/*UINT8*/ u8_pending;
        char ack_mask;
        char priority_mask;
        char in_service;
        char request;
        char status;
        char poll_status;
        char timer;
        char[] dma = new char[2];
        char[] ext = new char[4];
    };

    static class i186state {

        timer_state[] timer = timer_state.create(3);
        dma_state[] dma = dma_state.create(2);
        intr_state intr = new intr_state();
        mem_state mem = new mem_state();
    };
    static i186state i186_state;

    static final int DAC_BUFFER_SIZE_MASK = (DAC_BUFFER_SIZE - 1);

    static class dac_state {

        short value;
        short volume;
        int/*UINT32*/ frequency;
        int/*UINT32*/ step;
        int/*UINT32*/ fraction;

        short[] buffer = new short[DAC_BUFFER_SIZE];
        int/*UINT32*/ bufin;
        int/*UINT32*/ bufout;
        int/*UINT32*/ buftarget;

        public static dac_state[] create(int n) {
            dac_state[] a = new dac_state[n];
            for (int k = 0; k < n; k++) {
                a[k] = new dac_state();
            }
            return a;
        }
    }
    static dac_state[] dac = dac_state.create(8);

    static class counter_state {

        Object timer;
        int count;
        int/*UINT8*/ u8_mode;
        int/*UINT8*/ u8_readbyte;
        int/*UINT8*/ u8_writebyte;

        public static counter_state[] create(int n) {
            counter_state[] a = new counter_state[n];
            for (int k = 0; k < n; k++) {
                a[k] = new counter_state();
            }
            return a;
        }
    }
    static counter_state[] counter = counter_state.create(9);

    /**
     * ***********************************
     *
     * Manual DAC sound generation
     *
     ************************************
     */
    public static StreamInitPtr leland_i186_dac_update = new StreamInitPtr() {
        public void handler(int param, ShortPtr buffer, int length) {
            int i, j, start, stop;

            if (LOG_SHORTAGES != 0) {
                logerror("----\n");
            }

            /* reset the buffer */
            memset(buffer, 0, length * 2);

            /* if we're redline racer, we have more DACs */
            if (is_redline == 0) {
                start = 2;
                stop = 7;
            } else {
                start = 0;
                stop = 8;
            }

            /* loop over manual DAC channels */
            for (i = start; i < stop; i++) {
                dac_state d = dac[i];
                int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;

                /* if we have data, process it */
                if (count > 0) {
                    short[] base = d.buffer;
                    int source = d.bufout;
                    int frac = d.fraction;
                    int step = d.step;

                    /* sample-rate convert to the output frequency */
                    for (j = 0; j < length && count > 0; j++) {
                        buffer.write(j, (short) (buffer.read(j) + base[source]));
                        frac += step;
                        source += frac >> 24;
                        count -= frac >> 24;
                        frac &= 0xffffff;
                        source &= DAC_BUFFER_SIZE_MASK;
                    }

                    if (LOG_SHORTAGES != 0 && j < length) {
                        logerror("DAC #%d short by %d/%d samples\n", i, length - j, length);
                    }

                    /* update the DAC state */
                    d.fraction = frac;
                    d.bufout = source;
                }

                /* update the clock status */
                if (count < d.buftarget) {
                    if (LOG_OPTIMIZATION != 0) {
                        logerror("  - trigger due to clock active in update\n");
                    }
                    cpu_trigger.handler(CPU_RESUME_TRIGGER);
                    clock_active |= 1 << i;
                }
            }
        }
    };

    /**
     * ***********************************
     *
     * DMA-based DAC sound generation
     *
     ************************************
     */
    public static StreamInitPtr leland_i186_dma_update = new StreamInitPtr() {
        public void handler(int param, ShortPtr buffer, int length) {
            int i, j;

            /* reset the buffer */
            memset(buffer, 0, length * 2);

            /* loop over DMA buffers */
            for (i = 0; i < 2; i++) {
                dma_state d = i186_state.dma[i];

                /* check for enabled DMA */
                if ((d.control & 0x0002) != 0) {
                    /* make sure the parameters meet our expectations */
                    if ((d.control & 0xfe00) != 0x1600) {
                        logerror("Unexpected DMA control %02X\n", d.control);
                    } else if (is_redline == 0 && ((d.dest & 1) != 0 || (d.dest & 0x3f) > 0x0b)) {
                        logerror("Unexpected DMA destination %02X\n", d.dest);
                    } else if (is_redline != 0 && (d.dest & 0xf000) != 0x4000 && (d.dest & 0xf000) != 0x5000) {
                        logerror("Unexpected DMA destination %02X\n", d.dest);
                    } /* otherwise, we're ready for liftoff */ else {
                        UBytePtr base = memory_region(REGION_CPU3);
                        int source = d.source;
                        int count = d.count;
                        int which, frac, step, volume;

                        /* adjust for redline racer */
                        if (is_redline == 0) {
                            which = (d.dest & 0x3f) / 2;
                        } else {
                            which = (d.dest >> 9) & 7;
                        }

                        frac = dac[which].fraction;
                        step = dac[which].step;
                        volume = dac[which].volume;

                        /* sample-rate convert to the output frequency */
                        for (j = 0; j < length && count > 0; j++) {
                            buffer.write(j, (short) (buffer.read(j) + ((int) base.read(source) - 0x80) * volume));//buffer[j] += ((int)base[source] - 0x80) * volume;
                            frac += step;
                            source += frac >> 24;
                            count -= frac >> 24;
                            frac &= 0xffffff;
                        }

                        /* update the DMA state */
                        if (count > 0) {
                            d.source = source;
                            d.count = (char) count;
                        } else {
                            /* let the timer callback actually mark the transfer finished */
                            d.source = source + count - 1;
                            d.count = 1;
                            d.u8_finished = 1;
                        }

                        if (LOG_DMA != 0) {
                            logerror("DMA Generated %d samples - new count = %04X, source = %04X\n", j, d.count, d.source);
                        }

                        /* update the DAC state */
                        dac[which].fraction = frac;
                    }
                }
            }
        }
    };
    /*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Externally-driven DAC sound generation
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	static void leland_i186_extern_update(int param, INT16 *buffer, int length)
/*TODO*///	{
/*TODO*///		struct dac_state *d = &dac[7];
/*TODO*///		int count = ext_stop - ext_start;
/*TODO*///		int j;
/*TODO*///	
/*TODO*///		/* reset the buffer */
/*TODO*///		memset(buffer, 0, length * sizeof(INT16));
/*TODO*///	
/*TODO*///		/* if we have data, process it */
/*TODO*///		if (count > 0 && ext_active)
/*TODO*///		{
/*TODO*///			int source = ext_start;
/*TODO*///			int frac = d.fraction;
/*TODO*///			int step = d.step;
/*TODO*///	
/*TODO*///			/* sample-rate convert to the output frequency */
/*TODO*///			for (j = 0; j < length && count > 0; j++)
/*TODO*///			{
/*TODO*///				buffer[j] += ((INT16)ext_base[source] - 0x80) * d.volume;
/*TODO*///				frac += step;
/*TODO*///				source += frac >> 24;
/*TODO*///				count -= frac >> 24;
/*TODO*///				frac &= 0xffffff;
/*TODO*///			}
/*TODO*///	
/*TODO*///			/* update the DAC state */
/*TODO*///			d.fraction = frac;
/*TODO*///			ext_start = source;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
    /**
     * ***********************************
     *
     * Sound initialization
     *
     ************************************
     */
    public static ShStartPtr leland_i186_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            int i;

            /* bail if nothing to play */
            if (Machine.sample_rate == 0) {
                return 0;
            }

            /* determine which sound hardware is installed */
            has_ym2151 = 0;
            for (i = 0; i < MAX_SOUND; i++) {
                if (Machine.drv.sound[i].sound_type == SOUND_YM2151) {
                    has_ym2151 = 1;
                }
            }

            /* allocate separate streams for the DMA and non-DMA DACs */
            dma_stream = stream_init("80186 DMA-driven DACs", 100, Machine.sample_rate, 0, leland_i186_dma_update);
            nondma_stream = stream_init("80186 manually-driven DACs", 100, Machine.sample_rate, 0, leland_i186_dac_update);

            /* if we have a 2151, install an externally driven DAC stream */
            if (has_ym2151 != 0) {
                System.out.println("External");
                /*TODO*///			ext_base = memory_region(REGION_SOUND1);
/*TODO*///			extern_stream = stream_init("80186 externally-driven DACs", 100, Machine.sample_rate, 0, leland_i186_extern_update);
            }

            /* by default, we're not redline racer */
            is_redline = 0;
            return 0;
        }
    };

    public static ShStartPtr redline_i186_sh_start = new ShStartPtr() {
        public int handler(MachineSound msound) {
            int result = leland_i186_sh_start.handler(msound);
            is_redline = 1;
            return result;
        }
    };

    static void leland_i186_reset() {
        /* kill any live timers */
        if (i186_state.timer[0].int_timer != null) {
            timer_remove(i186_state.timer[0].int_timer);
        }
        if (i186_state.timer[1].int_timer != null) {
            timer_remove(i186_state.timer[1].int_timer);
        }
        if (i186_state.timer[2].int_timer != null) {
            timer_remove(i186_state.timer[2].int_timer);
        }
        if (i186_state.timer[0].time_timer != null) {
            timer_remove(i186_state.timer[0].time_timer);
        }
        if (i186_state.timer[1].time_timer != null) {
            timer_remove(i186_state.timer[1].time_timer);
        }
        if (i186_state.timer[2].time_timer != null) {
            timer_remove(i186_state.timer[2].time_timer);
        }
        if (i186_state.dma[0].finish_timer != null) {
            timer_remove(i186_state.dma[0].finish_timer);
        }
        if (i186_state.dma[1].finish_timer != null) {
            timer_remove(i186_state.dma[1].finish_timer);
        }

        /* reset the i186 state */
        i186_state = new i186state();

        /* reset the interrupt state */
        i186_state.intr.priority_mask = 0x0007;
        i186_state.intr.timer = 0x000f;
        i186_state.intr.dma[0] = 0x000f;
        i186_state.intr.dma[1] = 0x000f;
        i186_state.intr.ext[0] = 0x000f;
        i186_state.intr.ext[1] = 0x000f;
        i186_state.intr.ext[2] = 0x000f;
        i186_state.intr.ext[3] = 0x000f;
        /* reset the DAC and counter states as well */
        dac = dac_state.create(8);//		memset(&dac, 0, sizeof(dac));
        counter = counter_state.create(9);//		memset(&counter, 0, sizeof(counter));

        /* send a trigger in case we're suspended */
        if (LOG_OPTIMIZATION != 0) {
            logerror("  - trigger due to reset\n");
        }
        cpu_trigger.handler(CPU_RESUME_TRIGGER);
        total_reads = 0;

        /* reset the sound systems */
        sound_reset();
    }

    public static void leland_i186_sound_init() {
        /* RAM is multiply mapped in the first 128k of address space */
        cpu_setbank(6, new UBytePtr(ram_base));
        cpu_setbank(7, new UBytePtr(ram_base));

        /* reset the I86 registers */
        i186_state = new i186state();
        leland_i186_reset();

        /* reset our internal stuff */
        last_control = 0xf8;
        clock_active = 0;

        /* reset the external DAC */
        ext_start = 0;
        ext_stop = 0;
        ext_active = 0;
    }

    /**
     * ***********************************
     *
     * 80186 interrupt controller
     *
     ************************************
     */
    public static irqcallbacksPtr int_callback = new irqcallbacksPtr() {
        public int handler(int line) {
            if (LOG_INTERRUPTS != 0) {
                logerror("(%f) **** Acknowledged interrupt vector %02X\n", timer_get_time(), i186_state.intr.poll_status & 0x1f);
            }

            /* clear the interrupt */
            i86_set_irq_line(0, CLEAR_LINE);
            i186_state.intr.u8_pending = 0;

            /* clear the request and set the in-service bit */
            i186_state.intr.request &= ~i186_state.intr.ack_mask;

            i186_state.intr.in_service |= i186_state.intr.ack_mask;
            if (i186_state.intr.ack_mask == 0x0001) {
                switch (i186_state.intr.poll_status & 0x1f) {
                    case 0x08:
                        i186_state.intr.status &= ~0x01;
                        break;
                    case 0x12:
                        i186_state.intr.status &= ~0x02;
                        break;
                    case 0x13:
                        i186_state.intr.status &= ~0x04;
                        break;
                }
            }
            i186_state.intr.ack_mask = 0;

            /* a request no longer pending */
            i186_state.intr.poll_status &= ~0x8000;

            /* return the vector */
            return i186_state.intr.poll_status & 0x1f;
        }
    };

    static void update_interrupt_state() {
        int i, j, new_vector = 0;

        //if (LOG_INTERRUPTS != 0) logerror("update_interrupt_status: req=%02X stat=%02X serv=%02X\n", i186.intr.request, i186.intr.status, i186.intr.in_service);
        /* loop over priorities */
        for (i = 0; i <= i186_state.intr.priority_mask; i++) {
            /* note: by checking 4 bits, we also verify that the mask is off */
            if ((i186_state.intr.timer & 15) == i) {
                /* if we're already servicing something at this level, don't generate anything new */
                if ((i186_state.intr.in_service & 0x01) != 0) {
                    return;
                }

                /* if there's something pending, generate an interrupt */
                if ((i186_state.intr.status & 0x07) != 0) {
                    if ((i186_state.intr.status & 1) != 0) {
                        new_vector = 0x08;
                    } else if ((i186_state.intr.status & 2) != 0) {
                        new_vector = 0x12;
                    } else if ((i186_state.intr.status & 4) != 0) {
                        new_vector = 0x13;
                    } else {
                        //usrintf_showmessage("Invalid timer interrupt!");
                    }

                    /* set the clear mask and generate the int */
                    i186_state.intr.ack_mask = 0x0001;
                    generate_int(new_vector);
                    return;
                }
            }

            /* check DMA interrupts */
            for (j = 0; j < 2; j++) {
                if ((i186_state.intr.dma[j] & 15) == i) {
                    /* if we're already servicing something at this level, don't generate anything new */
                    if ((i186_state.intr.in_service & (0x04 << j)) != 0) {
                        return;
                    }

                    /* if there's something pending, generate an interrupt */
                    if ((i186_state.intr.request & (0x04 << j)) != 0) {
                        new_vector = 0x0a + j;

                        /* set the clear mask and generate the int */
                        i186_state.intr.ack_mask = (char) (0x0004 << j);
                        generate_int(new_vector);
                        return;
                    }
                }
            }

            /* check external interrupts */
            for (j = 0; j < 4; j++) {
                if ((i186_state.intr.ext[j] & 15) == i) {
                    /* if we're already servicing something at this level, don't generate anything new */
                    if ((i186_state.intr.in_service & (0x10 << j)) != 0) {
                        return;
                    }

                    /* if there's something pending, generate an interrupt */
                    if ((i186_state.intr.request & (0x10 << j)) != 0) {
                        /* otherwise, generate an interrupt for this request */
                        new_vector = 0x0c + j;

                        /* set the clear mask and generate the int */
                        i186_state.intr.ack_mask = (char) (0x0010 << j);
                        generate_int(new_vector);
                        return;
                    }
                }
            }
        }
        return;

    }

    static void generate_int(int new_vector) {
        /* generate the appropriate interrupt */
        i186_state.intr.poll_status = (char) (0x8000 | new_vector);
        if (i186_state.intr.u8_pending == 0) {
            cpu_set_irq_line(2, 0, ASSERT_LINE);
        }
        i186_state.intr.u8_pending = 1;
        cpu_trigger.handler(CPU_RESUME_TRIGGER);
        if (LOG_OPTIMIZATION != 0) {
            logerror("  - trigger due to interrupt pending\n");
        }
        if (LOG_INTERRUPTS != 0) {
            logerror("(%f) **** Requesting interrupt vector %02X\n", timer_get_time(), new_vector);
        }

    }

    static void handle_eoi(int data) {
        int i, j;

        /* specific case */
        if ((data & 0x8000) == 0) {
            /* turn off the appropriate in-service bit */
            switch (data & 0x1f) {
                case 0x08:
                    i186_state.intr.in_service &= ~0x01;
                    break;
                case 0x12:
                    i186_state.intr.in_service &= ~0x01;
                    break;
                case 0x13:
                    i186_state.intr.in_service &= ~0x01;
                    break;
                case 0x0a:
                    i186_state.intr.in_service &= ~0x04;
                    break;
                case 0x0b:
                    i186_state.intr.in_service &= ~0x08;
                    break;
                case 0x0c:
                    i186_state.intr.in_service &= ~0x10;
                    break;
                case 0x0d:
                    i186_state.intr.in_service &= ~0x20;
                    break;
                case 0x0e:
                    i186_state.intr.in_service &= ~0x40;
                    break;
                case 0x0f:
                    i186_state.intr.in_service &= ~0x80;
                    break;
                default:
                    logerror("%05X:ERROR - 80186 EOI with unknown vector %02X\n", cpu_get_pc(), data & 0x1f);
            }
            if (LOG_INTERRUPTS != 0) {
                logerror("(%f) **** Got EOI for vector %02X\n", timer_get_time(), data & 0x1f);
            }
        } /* non-specific case */ else {
            /* loop over priorities */
            for (i = 0; i <= 7; i++) {
                /* check for in-service timers */
                if ((i186_state.intr.timer & 7) == i && (i186_state.intr.in_service & 0x01) != 0) {
                    i186_state.intr.in_service &= ~0x01;
                    if (LOG_INTERRUPTS != 0) {
                        logerror("(%f) **** Got EOI for timer\n", timer_get_time());
                    }
                    return;
                }

                /* check for in-service DMA interrupts */
                for (j = 0; j < 2; j++) {
                    if ((i186_state.intr.dma[j] & 7) == i && (i186_state.intr.in_service & (0x04 << j)) != 0) {
                        i186_state.intr.in_service &= ~(0x04 << j);
                        if (LOG_INTERRUPTS != 0) {
                            logerror("(%f) **** Got EOI for DMA%d\n", timer_get_time(), j);
                        }
                        return;
                    }
                }

                /* check external interrupts */
                for (j = 0; j < 4; j++) {
                    if ((i186_state.intr.ext[j] & 7) == i && (i186_state.intr.in_service & (0x10 << j)) != 0) {
                        i186_state.intr.in_service &= ~(0x10 << j);
                        if (LOG_INTERRUPTS != 0) {
                            logerror("(%f) **** Got EOI for INT%d\n", timer_get_time(), j);
                        }
                        return;
                    }
                }
            }
        }
    }

    /*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	80186 internal timers
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
    public static timer_callback internal_timer_int = new timer_callback() {
        public void handler(int which) {
            timer_state t = i186_state.timer[which];

            if (LOG_TIMER != 0) {
                logerror("Hit interrupt callback for timer %d\n", which);
            }

            /* set the max count bit */
            t.control |= 0x0020;

            /* request an interrupt */
            if ((t.control & 0x2000) != 0) {
                i186_state.intr.status |= 0x01 << which;
                update_interrupt_state();
                if (LOG_TIMER != 0) {
                    logerror("  Generating timer interrupt\n");
                }
            }

            /* if we're continuous, reset */
            if ((t.control & 0x0001) != 0) {
                int count = t.maxA != 0 ? t.maxA : 0x10000;
                t.int_timer = timer_set((double) count * TIME_IN_HZ(2000000), which, internal_timer_int);
                if (LOG_TIMER != 0) {
                    logerror("  Repriming interrupt\n");
                }
            } else {
                t.int_timer = null;
            }
        }
    };

    static void internal_timer_sync(int which) {
        timer_state t = i186_state.timer[which];

        /* if we have a timing timer running, adjust the count */
        if (t.time_timer != null) {
            double current_time = timer_timeelapsed(t.time_timer);
            int net_clocks = (int) ((current_time - t.last_time) * 2000000.);
            t.last_time = current_time;

            /* set the max count bit if we passed the max */
            if ((int) t.count + net_clocks >= t.maxA) {
                t.control |= 0x0020;
            }

            /* set the new count */
            if (t.maxA != 0) {
                t.count = (char) ((t.count + net_clocks) % t.maxA);
            } else {
                t.count = (char) (t.count + net_clocks);
            }
        }
    }

    static void internal_timer_update(int which, int new_count, int new_maxA, int new_maxB, int new_control) {
        timer_state t = i186_state.timer[which];
        int update_int_timer = 0;

        /* if we have a new count and we're on, update things */
        if (new_count != -1) {
            if ((t.control & 0x8000) != 0) {
                internal_timer_sync(which);
                update_int_timer = 1;
            }
            t.count = (char) new_count;
        }

        /* if we have a new max and we're on, update things */
        if (new_maxA != -1 && new_maxA != t.maxA) {
            if ((t.control & 0x8000) != 0) {
                internal_timer_sync(which);
                update_int_timer = 1;
            }
            t.maxA = (char) new_maxA;
            if (new_maxA == 0) {
                new_maxA = 0x10000;
            }

            /* redline racer controls nothing externally? */
            if (is_redline != 0) {
            } /* on the common board, timer 0 controls the 10-bit DAC frequency */ else if (which == 0) {
                set_dac_frequency(6, 2000000 / new_maxA);
            } /* timer 1 controls the externally driven DAC on Indy Heat/WSF */ else if (which == 1 && has_ym2151 != 0) {
                set_dac_frequency(7, 2000000 / (new_maxA * 2));
            }
        }

        /* if we have a new max and we're on, update things */
        if (new_maxB != -1 && new_maxB != t.maxB) {
            if ((t.control & 0x8000) != 0) {
                internal_timer_sync(which);
                update_int_timer = 1;
            }
            t.maxB = (char) new_maxB;
            if (new_maxB == 0) {
                new_maxB = 0x10000;
            }

            /* timer 1 controls the externally driven DAC on Indy Heat/WSF */
 /* they alternate the use of maxA and maxB in a way that makes no */
 /* sense according to the 80186 documentation! */
            if (which == 1 && has_ym2151 != 0) {
                set_dac_frequency(7, 2000000 / (new_maxB * 2));
            }
        }

        /* handle control changes */
        if (new_control != -1) {
            int diff;

            /* merge back in the bits we don't modify */
            new_control = (new_control & ~0x1fc0) | (t.control & 0x1fc0);

            /* handle the /INH bit */
            if ((new_control & 0x4000) == 0) {
                new_control = (new_control & ~0x8000) | (t.control & 0x8000);
            }
            new_control &= ~0x4000;

            /* check for control bits we don't handle */
            diff = new_control ^ t.control;
            if ((diff & 0x001c) != 0) {
                logerror("%05X:ERROR! - unsupported timer mode %04X\n", new_control);
            }

            /* if we have real changes, update things */
            if (diff != 0) {
                /* if we're going off, make sure our timers are gone */
                if ((diff & 0x8000) != 0 && (new_control & 0x8000) == 0) {
                    /* compute the final count */
                    internal_timer_sync(which);

                    /* nuke the timer and force the interrupt timer to be recomputed */
                    if (t.time_timer != null) {
                        timer_remove(t.time_timer);
                    }
                    t.time_timer = null;
                    update_int_timer = 1;
                } /* if we're going on, start the timers running */ else if ((diff & 0x8000) != 0 && (new_control & 0x8000) != 0) {
                    /* start the timing */
                    t.time_timer = timer_set(TIME_NEVER, 0, null);
                    update_int_timer = 1;
                }

                /* if something about the interrupt timer changed, force an update */
                if ((diff & 0x8000) == 0 && (diff & 0x2000) != 0) {
                    internal_timer_sync(which);
                    update_int_timer = 1;
                }
            }

            /* set the new control register */
            t.control = (char) new_control;
        }

        /* update the interrupt timer */
 /* kludge: the YM2151 games sometimes crank timer 1 really high, and leave interrupts */
 /* enabled, even though the handler for timer 1 does nothing. To alleviate this, we */
 /* just ignore it */
        if (has_ym2151 == 0 || which != 1) {
            if (update_int_timer != 0) {
                if (t.int_timer != null) {
                    timer_remove(t.int_timer);
                }
                if ((t.control & 0x8000) != 0 && (t.control & 0x2000) != 0) {
                    int diff = t.maxA - t.count;
                    if (diff <= 0) {
                        diff += 0x10000;
                    }
                    t.int_timer = timer_set((double) diff * TIME_IN_HZ(2000000), which, internal_timer_int);
                    if (LOG_TIMER != 0) {
                        logerror("Set interrupt timer for %d\n", which);
                    }
                } else {
                    t.int_timer = null;
                }
            }
        }
    }

    /**
     * ***********************************
     *
     * 80186 internal DMA
     *
     ************************************
     */
    public static timer_callback dma_timer_callback = new timer_callback() {
        public void handler(int which) {
            dma_state d = i186_state.dma[which];

            /* force an update and see if we're really done */
            stream_update(dma_stream, 0);

            /* complete the status update */
            d.control &= ~0x0002;
            d.source += d.count;
            d.count = 0;

            /* check for interrupt generation */
            if ((d.control & 0x0100) != 0) {
                //if (LOG_DMA != 0) logerror("DMA%d timer callback - requesting interrupt: count = %04X, source = %04X\n", which, d.count, d.source);
                i186_state.intr.request |= 0x04 << which;
                update_interrupt_state();
            }
            d.finish_timer = null;
        }
    };

    static void update_dma_control(int which, int new_control) {
        dma_state d = i186_state.dma[which];
        int diff;

        /* handle the CHG bit */
        if ((new_control & 0x0004) == 0) {
            new_control = (new_control & ~0x0002) | (d.control & 0x0002);
        }
        new_control &= ~0x0004;

        /* check for control bits we don't handle */
        diff = new_control ^ d.control;
        if ((diff & 0x6811) != 0) {
            logerror("%05X:ERROR! - unsupported DMA mode %04X\n", new_control);
        }

        /* if we're going live, set a timer */
        if ((diff & 0x0002) != 0 && (new_control & 0x0002) != 0) {
            /* make sure the parameters meet our expectations */
            if ((new_control & 0xfe00) != 0x1600) {
                logerror("Unexpected DMA control %02X\n", new_control);
            } else if (is_redline == 0 && ((d.dest & 1) != 0 || (d.dest & 0x3f) > 0x0b)) {
                logerror("Unexpected DMA destination %02X\n", d.dest);
            } else if (is_redline != 0 && (d.dest & 0xf000) != 0x4000 && (d.dest & 0xf000) != 0x5000) {
                logerror("Unexpected DMA destination %02X\n", d.dest);
            } /* otherwise, set a timer */ else {
                int count = d.count;
                int dacnum;

                /* adjust for redline racer */
                if (is_redline == 0) {
                    dacnum = (d.dest & 0x3f) / 2;
                } else {
                    dacnum = (d.dest >> 9) & 7;
                    dac[dacnum].volume = (short) ((d.dest & 0x1fe) / 2 / DAC_VOLUME_SCALE);
                }

                //if (LOG_DMA != 0) logerror("Initiated DMA %d - count = %04X, source = %04X, dest = %04X\n", which, d.count, d.source, d.dest);
                if (d.finish_timer != null) {
                    timer_remove(d.finish_timer);
                }
                d.u8_finished = 0;
                d.finish_timer = timer_set(TIME_IN_HZ(dac[dacnum].frequency) * (double) count, which, dma_timer_callback);
            }
        }

        /* set the new control register */
        d.control = (char) new_control;
    }

    /**
     * ***********************************
     *
     * 80186 internal I/O reads
     *
     ************************************
     */
    public static ReadHandlerPtr i186_internal_port_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int shift = 8 * (offset & 1);
            int temp, which;

            switch (offset & ~1) {
                case 0x22:
                    logerror("%05X:ERROR - read from 80186 EOI\n", cpu_get_pc());
                    break;

                case 0x24:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 interrupt poll\n", cpu_get_pc());
                    }
                    if ((i186_state.intr.poll_status & 0x8000) != 0) {
                        int_callback.handler(0);
                    }
                    return (i186_state.intr.poll_status >> shift) & 0xff;

                case 0x26:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 interrupt poll status\n", cpu_get_pc());
                    }
                    return (i186_state.intr.poll_status >> shift) & 0xff;

                case 0x28:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 interrupt mask\n", cpu_get_pc());
                    }
                    temp = (i186_state.intr.timer >> 3) & 0x01;
                    temp |= (i186_state.intr.dma[0] >> 1) & 0x04;
                    temp |= (i186_state.intr.dma[1] >> 0) & 0x08;
                    temp |= (i186_state.intr.ext[0] << 1) & 0x10;
                    temp |= (i186_state.intr.ext[1] << 2) & 0x20;
                    temp |= (i186_state.intr.ext[2] << 3) & 0x40;
                    temp |= (i186_state.intr.ext[3] << 4) & 0x80;
                    return (temp >> shift) & 0xff;

                case 0x2a:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 interrupt priority mask\n", cpu_get_pc());
                    }
                    return (i186_state.intr.priority_mask >> shift) & 0xff;

                case 0x2c:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 interrupt in-service\n", cpu_get_pc());
                    }
                    return (i186_state.intr.in_service >> shift) & 0xff;

                case 0x2e:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 interrupt request\n", cpu_get_pc());
                    }
                    temp = i186_state.intr.request & ~0x0001;
                    if ((i186_state.intr.status & 0x0007) != 0) {
                        temp |= 1;
                    }
                    return (temp >> shift) & 0xff;

                case 0x30:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 interrupt status\n", cpu_get_pc());
                    }
                    return (i186_state.intr.status >> shift) & 0xff;

                case 0x32:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 timer interrupt control\n", cpu_get_pc());
                    }
                    return (i186_state.intr.timer >> shift) & 0xff;

                case 0x34:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA 0 interrupt control\n", cpu_get_pc());
                    }
                    return (i186_state.intr.dma[0] >> shift) & 0xff;

                case 0x36:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA 1 interrupt control\n", cpu_get_pc());
                    }
                    return (i186_state.intr.dma[1] >> shift) & 0xff;

                case 0x38:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 INT 0 interrupt control\n", cpu_get_pc());
                    }
                    return (i186_state.intr.ext[0] >> shift) & 0xff;

                case 0x3a:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 INT 1 interrupt control\n", cpu_get_pc());
                    }
                    return (i186_state.intr.ext[1] >> shift) & 0xff;

                case 0x3c:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 INT 2 interrupt control\n", cpu_get_pc());
                    }
                    return (i186_state.intr.ext[2] >> shift) & 0xff;

                case 0x3e:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 INT 3 interrupt control\n", cpu_get_pc());
                    }
                    return (i186_state.intr.ext[3] >> shift) & 0xff;

                case 0x50:
                case 0x58:
                case 0x60:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 Timer %d count\n", cpu_get_pc(), (offset - 0x50) / 8);
                    }
                    which = (offset - 0x50) / 8;
                    if ((offset & 1) == 0) {
                        internal_timer_sync(which);
                    }
                    return (i186_state.timer[which].count >> shift) & 0xff;

                case 0x52:
                case 0x5a:
                case 0x62:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 Timer %d max A\n", cpu_get_pc(), (offset - 0x50) / 8);
                    }
                    which = (offset - 0x50) / 8;
                    return (i186_state.timer[which].maxA >> shift) & 0xff;

                case 0x54:
                case 0x5c:
                    logerror("%05X:read 80186 Timer %d max B\n", cpu_get_pc(), (offset - 0x50) / 8);
                    which = (offset - 0x50) / 8;
                    return (i186_state.timer[which].maxB >> shift) & 0xff;

                case 0x56:
                case 0x5e:
                case 0x66:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 Timer %d control\n", cpu_get_pc(), (offset - 0x50) / 8);
                    }
                    which = (offset - 0x50) / 8;
                    return (i186_state.timer[which].control >> shift) & 0xff;

                case 0xa0:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 upper chip select\n", cpu_get_pc());
                    }
                    return (i186_state.mem.upper >> shift) & 0xff;

                case 0xa2:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 lower chip select\n", cpu_get_pc());
                    }
                    return (i186_state.mem.lower >> shift) & 0xff;

                case 0xa4:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 peripheral chip select\n", cpu_get_pc());
                    }
                    return (i186_state.mem.peripheral >> shift) & 0xff;

                case 0xa6:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 middle chip select\n", cpu_get_pc());
                    }
                    return (i186_state.mem.middle >> shift) & 0xff;

                case 0xa8:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 middle P chip select\n", cpu_get_pc());
                    }
                    return (i186_state.mem.middle_size >> shift) & 0xff;

                case 0xc0:
                case 0xd0:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA%d lower source address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    return (i186_state.dma[which].source >> shift) & 0xff;

                case 0xc2:
                case 0xd2:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA%d upper source address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    return (i186_state.dma[which].source >> (shift + 16)) & 0xff;

                case 0xc4:
                case 0xd4:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA%d lower dest address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    return (i186_state.dma[which].dest >> shift) & 0xff;

                case 0xc6:
                case 0xd6:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA%d upper dest address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    return (i186_state.dma[which].dest >> (shift + 16)) & 0xff;

                case 0xc8:
                case 0xd8:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA%d transfer count\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    return (i186_state.dma[which].count >> shift) & 0xff;

                case 0xca:
                case 0xda:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:read 80186 DMA%d control\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    return (i186_state.dma[which].control >> shift) & 0xff;

                default:
                    logerror("%05X:read 80186 port %02X\n", cpu_get_pc(), offset);
                    break;
            }
            return 0x00;
        }
    };
    /**
     * ***********************************
     *
     * 80186 internal I/O writes
     *
     ************************************
     */
    static int/*UINT8*/ u8_even_byte;
    public static WriteHandlerPtr i186_internal_port_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int temp, which;

            /* warning: this assumes all port writes here are word-sized */
            if ((offset & 1) == 0) {
                u8_even_byte = data & 0xFF;
                return;
            }
            data = ((data & 0xff) << 8) | u8_even_byte;

            switch (offset & ~1) {
                case 0x22:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 EOI = %04X\n", cpu_get_pc(), data);
                    }
                    handle_eoi(0x8000);
                    update_interrupt_state();
                    break;

                case 0x24:
                    logerror("%05X:ERROR - write to 80186 interrupt poll = %04X\n", cpu_get_pc(), data);
                    break;

                case 0x26:
                    logerror("%05X:ERROR - write to 80186 interrupt poll status = %04X\n", cpu_get_pc(), data);
                    break;

                case 0x28:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 interrupt mask = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.timer = (char) ((i186_state.intr.timer & ~0x08) | ((data << 3) & 0x08));
                    i186_state.intr.dma[0] = (char) ((i186_state.intr.dma[0] & ~0x08) | ((data << 1) & 0x08));
                    i186_state.intr.dma[1] = (char) ((i186_state.intr.dma[1] & ~0x08) | ((data << 0) & 0x08));
                    i186_state.intr.ext[0] = (char) ((i186_state.intr.ext[0] & ~0x08) | ((data >> 1) & 0x08));
                    i186_state.intr.ext[1] = (char) ((i186_state.intr.ext[1] & ~0x08) | ((data >> 2) & 0x08));
                    i186_state.intr.ext[2] = (char) ((i186_state.intr.ext[2] & ~0x08) | ((data >> 3) & 0x08));
                    i186_state.intr.ext[3] = (char) ((i186_state.intr.ext[3] & ~0x08) | ((data >> 4) & 0x08));
                    update_interrupt_state();
                    break;

                case 0x2a:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 interrupt priority mask = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.priority_mask = (char) (data & 0x0007);
                    update_interrupt_state();
                    break;

                case 0x2c:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 interrupt in-service = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.in_service = (char) (data & 0x00ff);
                    update_interrupt_state();
                    break;

                case 0x2e:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 interrupt request = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.request = (char) ((i186_state.intr.request & ~0x00c0) | (data & 0x00c0));
                    update_interrupt_state();
                    break;

                case 0x30:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:WARNING - wrote to 80186 interrupt status = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.status = (char) ((i186_state.intr.status & ~0x8007) | (data & 0x8007));
                    update_interrupt_state();
                    break;

                case 0x32:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 timer interrupt contol = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.timer = (char) (data & 0x000f);
                    break;

                case 0x34:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 DMA 0 interrupt control = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.dma[0] = (char) (data & 0x000f);
                    break;

                case 0x36:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 DMA 1 interrupt control = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.dma[1] = (char) (data & 0x000f);
                    break;

                case 0x38:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 INT 0 interrupt control = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.ext[0] = (char) (data & 0x007f);
                    break;

                case 0x3a:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 INT 1 interrupt control = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.ext[1] = (char) (data & 0x007f);
                    break;

                case 0x3c:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 INT 2 interrupt control = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.ext[2] = (char) (data & 0x001f);
                    break;

                case 0x3e:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 INT 3 interrupt control = %04X\n", cpu_get_pc(), data);
                    }
                    i186_state.intr.ext[3] = (char) (data & 0x001f);
                    break;

                case 0x50:
                case 0x58:
                case 0x60:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 Timer %d count = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
                    }
                    which = (offset - 0x50) / 8;
                    internal_timer_update(which, data, -1, -1, -1);
                    break;

                case 0x52:
                case 0x5a:
                case 0x62:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 Timer %d max A = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
                    }
                    which = (offset - 0x50) / 8;
                    internal_timer_update(which, -1, data, -1, -1);
                    break;

                case 0x54:
                case 0x5c:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 Timer %d max B = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
                    }
                    which = (offset - 0x50) / 8;
                    internal_timer_update(which, -1, -1, data, -1);
                    break;

                case 0x56:
                case 0x5e:
                case 0x66:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 Timer %d control = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
                    }
                    which = (offset - 0x50) / 8;
                    internal_timer_update(which, -1, -1, -1, data);
                    break;

                case 0xa0:
                    //if (LOG_PORTS != 0) logerror("%05X:80186 upper chip select = %04X\n", cpu_get_pc(), data);
                    i186_state.mem.upper = (char) (data | 0xc038);
                    break;

                case 0xa2:
                    //if (LOG_PORTS != 0) logerror("%05X:80186 lower chip select = %04X\n", cpu_get_pc(), data);
                    i186_state.mem.lower = (char) ((data & 0x3fff) | 0x0038);
                    break;

                case 0xa4:
                    //if (LOG_PORTS != 0) logerror("%05X:80186 peripheral chip select = %04X\n", cpu_get_pc(), data);
                    i186_state.mem.peripheral = (char) (data | 0x0038);
                    break;

                case 0xa6:
                    //if (LOG_PORTS != 0) logerror("%05X:80186 middle chip select = %04X\n", cpu_get_pc(), data);
                    i186_state.mem.middle = (char) (data | 0x01f8);
                    break;

                case 0xa8:
                    //if (LOG_PORTS != 0) logerror("%05X:80186 middle P chip select = %04X\n", cpu_get_pc(), data);
                    i186_state.mem.middle_size = (char) (data | 0x8038);

                    temp = (i186_state.mem.peripheral & 0xffc0) << 4;
                    if ((i186_state.mem.middle_size & 0x0040) != 0) {
                        install_mem_read_handler(2, temp, temp + 0x2ff, peripheral_r);
                        install_mem_write_handler(2, temp, temp + 0x2ff, peripheral_w);
                    } else {
                        temp &= 0xffff;
                        install_port_read_handler(2, temp, temp + 0x2ff, peripheral_r);
                        install_port_write_handler(2, temp, temp + 0x2ff, peripheral_w);
                    }

                    /* we need to do this at a time when the I86 context is swapped in */
 /* this register is generally set once at startup and never again, so it's a good */
 /* time to set it up */
                    i86_set_irq_callback(int_callback);
                    break;

                case 0xc0:
                case 0xd0:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 DMA%d lower source address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    i186_state.dma[which].source = (i186_state.dma[which].source & ~0x0ffff) | (data & 0x0ffff);
                    break;

                case 0xc2:
                case 0xd2:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 DMA%d upper source address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    i186_state.dma[which].source = (i186_state.dma[which].source & ~0xf0000) | ((data << 16) & 0xf0000);
                    break;

                case 0xc4:
                case 0xd4:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 DMA%d lower dest address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    i186_state.dma[which].dest = (i186_state.dma[which].dest & ~0x0ffff) | (data & 0x0ffff);
                    break;

                case 0xc6:
                case 0xd6:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 DMA%d upper dest address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    i186_state.dma[which].dest = (i186_state.dma[which].dest & ~0xf0000) | ((data << 16) & 0xf0000);
                    break;

                case 0xc8:
                case 0xd8:
                    if (LOG_PORTS != 0) {
                        logerror("%05X:80186 DMA%d transfer count = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
                    }
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    i186_state.dma[which].count = (char) data;
                    break;

                case 0xca:
                case 0xda:
                    //if (LOG_PORTS != 0) logerror("%05X:80186 DMA%d control = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
                    which = (offset - 0xc0) / 0x10;
                    stream_update(dma_stream, 0);
                    update_dma_control(which, data);
                    break;

                case 0xfe:
                    //if (LOG_PORTS != 0) logerror("%05X:80186 relocation register = %04X\n", cpu_get_pc(), data);

                    /* we assume here there that this doesn't happen too often */
 /* plus, we can't really remove the old memory range, so we also assume that it's */
 /* okay to leave us mapped where we were */
                    temp = (data & 0x0fff) << 8;
                    if ((data & 0x1000) != 0) {
                        install_mem_read_handler(2, temp, temp + 0xff, i186_internal_port_r);
                        install_mem_write_handler(2, temp, temp + 0xff, i186_internal_port_w);
                    } else {
                        temp &= 0xffff;
                        install_port_read_handler(2, temp, temp + 0xff, i186_internal_port_r);
                        install_port_write_handler(2, temp, temp + 0xff, i186_internal_port_w);
                    }
                    /*			usrintf_showmessage("Sound CPU reset");*/
                    break;

                default:
                    System.out.println(Integer.toHexString(offset & ~1));

                    throw new UnsupportedOperationException("Unsupported");
                //logerror("%05X:80186 port %02X = %04X\n", cpu_get_pc(), offset, data);
                //break;
            }
        }
    };

    /**
     * ***********************************
     *
     * 8254 PIT accesses
     *
     ************************************
     */
    static void counter_update_count(int which) {
        /* only update if the timer is running */
        if (counter[which].timer != null) {
            /* determine how many 2MHz cycles are remaining */
            int count = (int) (timer_timeleft(counter[which].timer) / TIME_IN_HZ(2000000));
            counter[which].count = (count < 0) ? 0 : count;
        }
    }
    public static ReadHandlerPtr pit8254_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            counter_state ctr;
            int which = offset / 0x80;
            int reg = (offset / 2) & 3;

            /* ignore odd offsets */
            if ((offset & 1) != 0) {
                return 0;
            }

            /* switch off the register */
            switch (offset & 3) {
                case 0:
                case 1:
                case 2:
                    /* warning: assumes LSB/MSB addressing and no latching! */
                    which = (which * 3) + reg;
                    ctr = counter[which];

                    /* update the count */
                    counter_update_count(which);

                    /* return the LSB */
                    if (counter[which].u8_readbyte == 0) {
                        counter[which].u8_readbyte = 1;
                        return counter[which].count & 0xff;
                    } /* write the MSB and reset the counter */ else {
                        counter[which].u8_readbyte = 0;
                        return (counter[which].count >> 8) & 0xff;
                    }
                //break;
            }
            return 0;
        }
    };
    public static WriteHandlerPtr pit8254_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            counter_state ctr;
            int which = offset / 0x80;
            int reg = (offset / 2) & 3;

            /* ignore odd offsets */
            if ((offset & 1) != 0) {
                return;
            }

            /* switch off the register */
            switch (reg) {
                case 0:
                case 1:
                case 2:
                    /* warning: assumes LSB/MSB addressing and no latching! */
                    which = (which * 3) + reg;
                    ctr = counter[which];

                    /* write the LSB */
                    if (ctr.u8_writebyte == 0) {
                        ctr.count = (ctr.count & 0xff00) | (data & 0x00ff);
                        ctr.u8_writebyte = 1;
                    } /* write the MSB and reset the counter */ else {
                        ctr.count = (ctr.count & 0x00ff) | ((data << 8) & 0xff00);
                        ctr.u8_writebyte = 0;

                        /* treat 0 as $10000 */
                        if (ctr.count == 0) {
                            ctr.count = 0x10000;
                        }

                        /* reset/start the timer */
                        if (ctr.timer != null) {
                            timer_reset(ctr.timer, TIME_NEVER);
                        } else {
                            ctr.timer = timer_set(TIME_NEVER, 0, null);
                        }

                        if (LOG_PIT != 0) {
                            logerror("PIT counter %d set to %d (%d Hz)\n", which, ctr.count, 4000000 / ctr.count);
                        }

                        /* set the frequency of the associated DAC */
                        if (is_redline == 0) {
                            set_dac_frequency(which, 4000000 / ctr.count);
                        } else {
                            if (which < 5) {
                                set_dac_frequency(which, 7000000 / ctr.count);
                            } else if (which == 6) {
                                set_dac_frequency(5, 7000000 / ctr.count);
                                set_dac_frequency(6, 7000000 / ctr.count);
                                set_dac_frequency(7, 7000000 / ctr.count);
                            }
                        }
                    }
                    break;

                case 3:
                    /* determine which counter */
                    if ((data & 0xc0) == 0xc0) {
                        break;
                    }
                    which = (which * 3) + (data >> 6);
                    ctr = counter[which];

                    /* set the mode */
                    ctr.u8_mode = (data >> 1) & 7;
                    break;
            }
        }
    };
    /**
     * ***********************************
     *
     * External 80186 control
     *
     ************************************
     */
    public static WriteHandlerPtr leland_i86_control_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* see if anything changed */
            int diff = (last_control ^ data) & 0xf8;
            if (diff == 0) {
                return;
            }
            last_control = data;

            //if (LOG_COMM != 0)
            //{
            //	logerror("%04X:I86 control = %02X", cpu_getpreviouspc(), data);
            //	if (!(data & 0x80)) logerror("  /RESET");
            //	if (!(data & 0x40)) logerror("  ZNMI");
            //	if (!(data & 0x20)) logerror("  INT0");
            //	if (!(data & 0x10)) logerror("  /TEST");
            //	if (!(data & 0x08)) logerror("  INT1");
            //	logerror("\n");
            //}
            /* /RESET */
            cpu_set_reset_line(2, (data & 0x80) != 0 ? CLEAR_LINE : ASSERT_LINE);

            /* /NMI */
 /* 	If the master CPU doesn't get a response by the time it's ready to send
		the next command, it uses an NMI to force the issue; unfortunately, this
		seems to really screw up the sound system. It turns out it's better to
		just wait for the original interrupt to occur naturally */
 /*	cpu_set_nmi_line  (2, data & 0x40  ? CLEAR_LINE : ASSERT_LINE);*/
 /* INT0 */
            if ((data & 0x20) != 0) {
                if (LATCH_INTS == 0) {
                    i186_state.intr.request &= ~0x10;
                }
            } else if ((i186_state.intr.ext[0] & 0x10) != 0) {
                i186_state.intr.request |= 0x10;
            } else if ((diff & 0x20) != 0) {
                i186_state.intr.request |= 0x10;
            }

            /* INT1 */
            if ((data & 0x08) != 0) {
                if (LATCH_INTS == 0) {
                    i186_state.intr.request &= ~0x20;
                }
            } else if ((i186_state.intr.ext[1] & 0x10) != 0) {
                i186_state.intr.request |= 0x20;
            } else if ((diff & 0x08) != 0) {
                i186_state.intr.request |= 0x20;
            }

            /* handle reset here */
            if ((diff & 0x80) != 0 && (data & 0x80) != 0) {
                leland_i186_reset();
            }

            update_interrupt_state();
        }
    };
    /**
     * ***********************************
     *
     * Sound command handling
     *
     ************************************
     */
    public static timer_callback command_lo_sync = new timer_callback() {
        public void handler(int data) {
            if (LOG_COMM != 0) {
                logerror("%04X:Write sound command latch lo = %02X\n", cpu_getpreviouspc(), data);
            }
            u8_sound_command[0] = data & 0xFF;
        }
    };
    public static WriteHandlerPtr leland_i86_command_lo_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            timer_set(TIME_NOW, data, command_lo_sync);
        }
    };
    public static WriteHandlerPtr leland_i86_command_hi_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (LOG_COMM != 0) {
                logerror("%04X:Write sound command latch hi = %02X\n", cpu_getpreviouspc(), data);
            }
            u8_sound_command[1] = data & 0xFF;
        }
    };
    public static ReadHandlerPtr main_to_sound_comm_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            if ((offset & 1) == 0) {
                if (LOG_COMM != 0) {
                    logerror("%05X:Read sound command latch lo = %02X\n", cpu_get_pc(), u8_sound_command[0]);
                }
                return u8_sound_command[0];
            } else {
                if (LOG_COMM != 0) {
                    logerror("%05X:Read sound command latch hi = %02X\n", cpu_get_pc(), u8_sound_command[1]);
                }
                return u8_sound_command[1];
            }
        }
    };
    /**
     * ***********************************
     *
     * Sound response handling
     *
     ************************************
     */
    public static timer_callback delayed_response_r = new timer_callback() {
        public void handler(int checkpc) {
            int pc = cpunum_get_reg(0, Z80_PC);
            int oldaf = cpunum_get_reg(0, Z80_AF);

            /* This is pretty cheesy, but necessary. Since the CPUs run in round-robin order,
		   synchronizing on the write to this register from the slave side does nothing.
		   In order to make sure the master CPU get the real response, we synchronize on
		   the read. However, the value we returned the first time around may not be
		   accurate, so after the system has synced up, we go back into the master CPUs
		   state and put the proper value into the A register. */
            if (pc == checkpc) {
                if (LOG_COMM != 0) {
                    logerror("(Updated sound response latch to %02X)\n", sound_response);
                }
                oldaf = (oldaf & 0x00ff) | (sound_response << 8);
                cpunum_set_reg(0, Z80_AF, oldaf);
            } else {
                logerror("ERROR: delayed_response_r - current PC = %04X, checkPC = %04X\n", pc, checkpc);
            }
        }
    };
    public static ReadHandlerPtr leland_i86_response_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            if (LOG_COMM != 0) {
                logerror("%04X:Read sound response latch = %02X\n", cpu_getpreviouspc(), sound_response);
            }

            /* if sound is disabled, toggle between FF and 00 */
            if (Machine.sample_rate == 0) {
                return sound_response ^= 0xff;
            } else {
                /* synchronize the response */
                timer_set(TIME_NOW, cpu_getpreviouspc() + 2, delayed_response_r);
                return sound_response;
            }
        }
    };
    public static WriteHandlerPtr sound_to_main_comm_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (LOG_COMM != 0) {
                logerror("%05X:Write sound response latch = %02X\n", cpu_get_pc(), data);
            }
            sound_response = data;
        }
    };

    /**
     * ***********************************
     *
     * Low-level DAC I/O
     *
     ************************************
     */
    static void set_dac_frequency(int which, int frequency) {
        dac_state d = dac[which];
        int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;

        /* set the frequency of the associated DAC */
        d.frequency = frequency;
        d.step = (int) ((double) frequency * (double) (1 << 24) / (double) Machine.sample_rate);

        /* also determine the target buffer size */
        d.buftarget = dac[which].frequency / 60 + 50;
        if (d.buftarget > DAC_BUFFER_SIZE - 1) {
            d.buftarget = DAC_BUFFER_SIZE - 1;
        }

        /* reevaluate the count */
        if (count > d.buftarget) {
            clock_active &= ~(1 << which);
        } else if (count < d.buftarget) {
            if (LOG_OPTIMIZATION != 0) {
                logerror("  - trigger due to clock active in set_dac_frequency\n");
            }
            cpu_trigger.handler(CPU_RESUME_TRIGGER);
            clock_active |= 1 << which;
        }

        if (LOG_DAC != 0) {
            logerror("DAC %d frequency = %d, step = %08X\n", which, d.frequency, d.step);
        }
    }

    public static WriteHandlerPtr dac_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int which = offset / 2;
            dac_state d = dac[which];

            /* handle value changes */
            if ((offset & 1) == 0) {
                int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;

                /* set the new value */
                d.value = (short) ((short) data - 0x80);
                if (LOG_DAC != 0) {
                    logerror("%05X:DAC %d value = %02X\n", cpu_get_pc(), offset / 2, data);
                }

                /* if we haven't overflowed the buffer, add the value value to it */
                if (count < DAC_BUFFER_SIZE - 1) {
                    /* if this is the first byte, sync the stream */
                    if (count == 0) {
                        stream_update(nondma_stream, 0);
                    }

                    /* prescale by the volume */
                    d.buffer[d.bufin] = (short) (d.value * d.volume);
                    d.bufin = (d.bufin + 1) & DAC_BUFFER_SIZE_MASK;

                    /* update the clock status */
                    if (++count > d.buftarget) {
                        clock_active &= ~(1 << which);
                    }
                }
            } /* handle volume changes */ else {
                d.volume = (short) ((data ^ 0x00) / DAC_VOLUME_SCALE);
                if (LOG_DAC != 0) {
                    logerror("%05X:DAC %d volume = %02X\n", cpu_get_pc(), offset / 2, data);
                }
            }
        }
    };

    public static WriteHandlerPtr redline_dac_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int which = offset / 0x200;
            dac_state d = dac[which];
            int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;

            /* set the new value */
            d.value = (short) ((short) data - 0x80);

            /* if we haven't overflowed the buffer, add the value value to it */
            if (count < DAC_BUFFER_SIZE - 1) {
                /* if this is the first byte, sync the stream */
                if (count == 0) {
                    stream_update(nondma_stream, 0);
                }

                /* prescale by the volume */
                d.buffer[d.bufin] = (short) (d.value * d.volume);
                d.bufin = (d.bufin + 1) & DAC_BUFFER_SIZE_MASK;

                /* update the clock status */
                if (++count > d.buftarget) {
                    clock_active &= ~(1 << which);
                }
            }

            /* update the volume */
            d.volume = (short) ((offset & 0x1fe) / 2 / DAC_VOLUME_SCALE);
            if (LOG_DAC != 0) {
                logerror("%05X:DAC %d value = %02X, volume = %02X\n", cpu_get_pc(), which, data, (offset & 0x1fe) / 2);
            }
        }
    };
    static int/*UINT8*/ even_byte;
    public static WriteHandlerPtr dac_10bit_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            dac_state d = dac[6];
            int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;

            /* warning: this assumes all port writes here are word-sized */
 /* if the offset is even, just stash the value */
            if ((offset & 1) == 0) {
                even_byte = data & 0xFF;
                return;
            }
            data = ((data & 0xff) << 8) | even_byte;

            /* set the new value */
            d.value = (short) ((short) data - 0x200);
            if (LOG_DAC != 0) {
                logerror("%05X:DAC 10-bit value = %02X\n", cpu_get_pc(), data);
            }

            /* if we haven't overflowed the buffer, add the value value to it */
            if (count < DAC_BUFFER_SIZE - 1) {
                /* if this is the first byte, sync the stream */
                if (count == 0) {
                    stream_update(nondma_stream, 0);
                }

                /* prescale by the volume */
                d.buffer[d.bufin] = (short) (d.value * (0xff / DAC_VOLUME_SCALE / 2));
                d.bufin = (d.bufin + 1) & DAC_BUFFER_SIZE_MASK;

                /* update the clock status */
                if (++count > d.buftarget) {
                    clock_active &= ~0x40;
                }
            }
        }
    };

    public static WriteHandlerPtr ataxx_dac_control = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///		/* handle common offsets */
/*TODO*///		switch (offset)
/*TODO*///		{
/*TODO*///			case 0x00:
/*TODO*///			case 0x02:
/*TODO*///			case 0x04:
/*TODO*///				dac_w(offset, data);
/*TODO*///				return;
/*TODO*///	
/*TODO*///			case 0x06:
/*TODO*///				dac_w(1, ((data << 5) & 0xe0) | ((data << 2) & 0x1c) | (data & 0x03));
/*TODO*///				dac_w(3, ((data << 2) & 0xe0) | ((data >> 1) & 0x1c) | ((data >> 4) & 0x03));
/*TODO*///				dac_w(5, (data & 0xc0) | ((data >> 2) & 0x30) | ((data >> 4) & 0x0c) | ((data >> 6) & 0x03));
/*TODO*///				return;
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* if we have a YM2151 (and an external DAC), handle those offsets */
/*TODO*///		if (has_ym2151 != 0)
/*TODO*///		{
/*TODO*///			stream_update(extern_stream, 0);
/*TODO*///			switch (offset)
/*TODO*///			{
/*TODO*///				case 0x08:
/*TODO*///				case 0x09:
/*TODO*///					ext_active = 1;
/*TODO*///					if (LOG_EXTERN != 0) logerror("External DAC active\n");
/*TODO*///					return;
/*TODO*///	
/*TODO*///				case 0x0a:
/*TODO*///				case 0x0b:
/*TODO*///					ext_active = 0;
/*TODO*///					if (LOG_EXTERN != 0) logerror("External DAC inactive\n");
/*TODO*///					return;
/*TODO*///	
/*TODO*///				case 0x0c:
/*TODO*///					ext_start = (ext_start & 0xff00f) | ((data << 4) & 0x00ff0);
/*TODO*///					if (LOG_EXTERN != 0) logerror("External DAC start = %05X\n", ext_start);
/*TODO*///					return;
/*TODO*///	
/*TODO*///				case 0x0d:
/*TODO*///					ext_start = (ext_start & 0x00fff) | ((data << 12) & 0xff000);
/*TODO*///					if (LOG_EXTERN != 0) logerror("External DAC start = %05X\n", ext_start);
/*TODO*///					return;
/*TODO*///	
/*TODO*///				case 0x0e:
/*TODO*///					ext_stop = (ext_stop & 0xff00f) | ((data << 4) & 0x00ff0);
/*TODO*///					if (LOG_EXTERN != 0) logerror("External DAC stop = %05X\n", ext_stop);
/*TODO*///					return;
/*TODO*///	
/*TODO*///				case 0x0f:
/*TODO*///					ext_stop = (ext_stop & 0x00fff) | ((data << 12) & 0xff000);
/*TODO*///					if (LOG_EXTERN != 0) logerror("External DAC stop = %05X\n", ext_stop);
/*TODO*///					return;
/*TODO*///	
/*TODO*///				case 0x42:
/*TODO*///				case 0x43:
/*TODO*///					dac_w(offset - 0x42 + 14, data);
/*TODO*///					return;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		logerror("%05X:Unexpected peripheral write %d/%02X = %02X\n", cpu_get_pc(), 5, offset, data);
        }
    };

    /**
     * ***********************************
     *
     * Peripheral chip dispatcher
     *
     ************************************
     */
    public static ReadHandlerPtr peripheral_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int select = offset / 0x80;
            offset &= 0x7f;

            switch (select) {
                case 0:
                    if ((offset & 1) != 0) {
                        return 0;
                    }

                    /* we have to return 0 periodically so that they handle interrupts */
                    if ((++clock_tick & 7) == 0) {
                        return 0;
                    }

                    /* if we've filled up all the active channels, we can give this CPU a reset */
 /* until the next interrupt */
                     {
                        int/*UINT8*/ u8_result;

                        if (is_redline == 0) {
                            u8_result = ((clock_active >> 1) & 0x3e);
                        } else {
                            u8_result = ((clock_active << 1) & 0x7e);
                        }

                        if (i186_state.intr.u8_pending != 0 && active_mask != null && (active_mask.read() & u8_result) == 0 && ++total_reads > 100) {
                            if (LOG_OPTIMIZATION != 0) {
                                logerror("Suspended CPU: active_mask = %02X, result = %02X\n", active_mask.read(), u8_result);
                            }
                            cpu_spinuntil_trigger(CPU_RESUME_TRIGGER);
                        } else if (LOG_OPTIMIZATION != 0) {
                            if (i186_state.intr.u8_pending != 0) {
                                logerror("(can't suspend - interrupt pending)\n");
                            } else if (active_mask != null && (active_mask.read() & u8_result) != 0) {
                                logerror("(can't suspend: mask=%02X result=%02X\n", active_mask.read(), u8_result);
                            }
                        }

                        return u8_result & 0xFF;
                    }

                case 1:
                    return main_to_sound_comm_r.handler(offset);

                case 2:
                    return pit8254_r.handler(offset);

                case 3:
                    if (has_ym2151 == 0) {
                        return pit8254_r.handler(offset | 0x80);
                    } else {
                        return (offset & 1) != 0 ? 0 : YM2151_status_port_0_r.handler(offset);
                    }

                case 4:
                    if (is_redline != 0) {
                        return pit8254_r.handler(offset | 0x100);
                    } else {
                        logerror("%05X:Unexpected peripheral read %d/%02X\n", cpu_get_pc(), select, offset);
                    }
                    break;

                default:
                    logerror("%05X:Unexpected peripheral read %d/%02X\n", cpu_get_pc(), select, offset);
                    break;
            }
            return 0xff;
        }
    };
    public static WriteHandlerPtr peripheral_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int select = offset / 0x80;
            offset &= 0x7f;

            switch (select) {
                case 1:
                    sound_to_main_comm_w.handler(offset, data);
                    break;

                case 2:
                    pit8254_w.handler(offset, data);
                    break;

                case 3:
                    if (has_ym2151 == 0) {
                        pit8254_w.handler(offset | 0x80, data);
                    } else if (offset == 0) {
                        YM2151_register_port_0_w.handler(offset, data);
                    } else if (offset == 2) {
                        YM2151_data_port_0_w.handler(offset, data);
                    }
                    break;

                case 4:
                    if (is_redline != 0) {
                        pit8254_w.handler(offset | 0x100, data);
                    } else {
                        dac_10bit_w.handler(offset, data);
                    }
                    break;

                case 5:
                    /* Ataxx/WSF/Indy Heat only */
                    ataxx_dac_control.handler(offset, data);
                    break;

                default:
                    logerror("%05X:Unexpected peripheral write %d/%02X = %02X\n", cpu_get_pc(), select, offset, data);
                    break;
            }
        }
    };

    /**
     * ***********************************
     *
     * Optimizations
     *
     ************************************
     */
    public static void leland_i86_optimize_address(int offset) {
        if (offset != 0) {
            active_mask = new UBytePtr(memory_region(REGION_CPU3), offset);
        } else {
            active_mask = null;
        }
    }
    /**
     * ***********************************
     *
     * Game-specific handlers
     *
     ************************************
     */
    public static WriteHandlerPtr ataxx_i86_control_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* compute the bit-shuffled variants of the bits and then write them */
            int modified = ((data & 0x01) << 7)
                    | ((data & 0x02) << 5)
                    | ((data & 0x04) << 3)
                    | ((data & 0x08) << 1);
            leland_i86_control_w.handler(offset, modified);
        }
    };
    /**
     * ***********************************
     *
     * Sound CPU memory handlers
     *
     ************************************
     */
    public static MemoryReadAddress leland_i86_readmem[]
            = {
                new MemoryReadAddress(0x00000, 0x03fff, MRA_RAM),
                new MemoryReadAddress(0x0c000, 0x0ffff, MRA_BANK6), /* used by Ataxx */
                new MemoryReadAddress(0x1c000, 0x1ffff, MRA_BANK7), /* used by Super Offroad */
                new MemoryReadAddress(0x20000, 0xfffff, MRA_ROM),
                new MemoryReadAddress(-1) /* end of table */};

    public static MemoryWriteAddress leland_i86_writemem[]
            = {
                new MemoryWriteAddress(0x00000, 0x03fff, MWA_RAM, ram_base),
                new MemoryWriteAddress(0x0c000, 0x0ffff, MWA_BANK6),
                new MemoryWriteAddress(0x1c000, 0x1ffff, MWA_BANK7),
                new MemoryWriteAddress(0x20000, 0xfffff, MWA_ROM),
                new MemoryWriteAddress(-1) /* end of table */};

    public static IOReadPort leland_i86_readport[]
            = {
                new IOReadPort(0xff00, 0xffff, i186_internal_port_r),
                new IOReadPort(-1) /* end of table */};

    public static IOWritePort redline_i86_writeport[]
            = {
                new IOWritePort(0x6000, 0x6fff, redline_dac_w),
                new IOWritePort(0xff00, 0xffff, i186_internal_port_w),
                new IOWritePort(-1) /* end of table */};

    public static IOWritePort leland_i86_writeport[]
            = {
                new IOWritePort(0x0000, 0x000b, dac_w),
                new IOWritePort(0x0080, 0x008b, dac_w),
                new IOWritePort(0x00c0, 0x00cb, dac_w),
                new IOWritePort(0xff00, 0xffff, i186_internal_port_w),
                new IOWritePort(-1) /* end of table */};

    public static IOWritePort ataxx_i86_writeport[]
            = {
                new IOWritePort(0xff00, 0xffff, i186_internal_port_w),
                new IOWritePort(-1) /* end of table */};

}
