/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.sndhrdw;

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
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ReadHandlerPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ShStartPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ShStopPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.v037b7.cpu.z80.z80H.*;
import gr.codebb.arcadeflex.v037b7.mame.sndintrfH.MachineSound;
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

    /*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	2nd-4th generation sound
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	#define LOG_INTERRUPTS		0
/*TODO*///	#define LOG_DMA				0
/*TODO*///	#define LOG_SHORTAGES		0
/*TODO*///	#define LOG_TIMER			0
/*TODO*///	#define LOG_COMM			0
/*TODO*///	#define LOG_PORTS			0
/*TODO*///	#define LOG_DAC				0
/*TODO*///	#define LOG_EXTERN			0
/*TODO*///	#define LOG_PIT				0
/*TODO*///	#define LOG_OPTIMIZATION	0
/*TODO*///	
/*TODO*///	
/*TODO*///	/* according to the Intel manual, external interrupts are not latched */
/*TODO*///	/* however, I cannot get this system to work without latching them */
/*TODO*///	#define LATCH_INTS	1
/*TODO*///	
/*TODO*///	#define DAC_VOLUME_SCALE	4
/*TODO*///	#define CPU_RESUME_TRIGGER	7123
/*TODO*///	
/*TODO*///	
/*TODO*///	static int dma_stream;
/*TODO*///	static int nondma_stream;
/*TODO*///	static int extern_stream;
/*TODO*///	
    static UBytePtr ram_base=new UBytePtr();
/*TODO*///	static UINT8 has_ym2151;
    static int/*UINT8*/ is_redline;

    static int/*UINT8*/ last_control;
    /*TODO*///	static UINT8 clock_active;
/*TODO*///	static UINT8 clock_tick;
/*TODO*///	
    static int[]/*UINT8*/ u8_sound_command = new int[2];
    static int/*UINT8*/ sound_response;

    /*TODO*///	
/*TODO*///	static UINT32 ext_start;
/*TODO*///	static UINT32 ext_stop;
/*TODO*///	static UINT8 ext_active;
/*TODO*///	static UINT8 *ext_base;
/*TODO*///	
    public static UBytePtr active_mask;
/*TODO*///	static int total_reads;
/*TODO*///	
/*TODO*///	struct mem_state
/*TODO*///	{
/*TODO*///		UINT16	lower;
/*TODO*///		UINT16	upper;
/*TODO*///		UINT16	middle;
/*TODO*///		UINT16	middle_size;
/*TODO*///		UINT16	peripheral;
/*TODO*///	};
/*TODO*///	
/*TODO*///	struct timer_state
/*TODO*///	{
/*TODO*///		UINT16	control;
/*TODO*///		UINT16	maxA;
/*TODO*///		UINT16	maxB;
/*TODO*///		UINT16	count;
/*TODO*///		void *	int_timer;
/*TODO*///		void *	time_timer;
/*TODO*///		double	last_time;
/*TODO*///	};
/*TODO*///	
/*TODO*///	struct dma_state
/*TODO*///	{
/*TODO*///		UINT32	source;
/*TODO*///		UINT32	dest;
/*TODO*///		UINT16	count;
/*TODO*///		UINT16	control;
/*TODO*///		UINT8	finished;
/*TODO*///		void *	finish_timer;
/*TODO*///	};
/*TODO*///	
/*TODO*///	struct intr_state
/*TODO*///	{
/*TODO*///		UINT8	pending;
/*TODO*///		UINT16	ack_mask;
/*TODO*///		UINT16	priority_mask;
/*TODO*///		UINT16	in_service;
/*TODO*///		UINT16	request;
/*TODO*///		UINT16	status;
/*TODO*///		UINT16	poll_status;
/*TODO*///		UINT16	timer;
/*TODO*///		UINT16	dma[2];
/*TODO*///		UINT16	ext[4];
/*TODO*///	};
/*TODO*///	
/*TODO*///	static struct i186_state
/*TODO*///	{
/*TODO*///		struct timer_state	timer[3];
/*TODO*///		struct dma_state	dma[2];
/*TODO*///		struct intr_state	intr;
/*TODO*///		struct mem_state	mem;
/*TODO*///	} i186;
/*TODO*///	
/*TODO*///	
/*TODO*///	#define DAC_BUFFER_SIZE			1024
/*TODO*///	#define DAC_BUFFER_SIZE_MASK	(DAC_BUFFER_SIZE - 1)
/*TODO*///	static struct dac_state
/*TODO*///	{
/*TODO*///		INT16	value;
/*TODO*///		INT16	volume;
/*TODO*///		UINT32	frequency;
/*TODO*///		UINT32	step;
/*TODO*///		UINT32	fraction;
/*TODO*///	
/*TODO*///		INT16	buffer[DAC_BUFFER_SIZE];
/*TODO*///		UINT32	bufin;
/*TODO*///		UINT32	bufout;
/*TODO*///		UINT32	buftarget;
/*TODO*///	} dac[8];
/*TODO*///	
/*TODO*///	static struct counter_state
/*TODO*///	{
/*TODO*///		void *timer;
/*TODO*///		INT32 count;
/*TODO*///		UINT8 mode;
/*TODO*///		UINT8 readbyte;
/*TODO*///		UINT8 writebyte;
/*TODO*///	} counter[9];
/*TODO*///	
/*TODO*///	static void set_dac_frequency(int which, int frequency);
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Manual DAC sound generation
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	static void leland_i186_dac_update(int param, INT16 *buffer, int length)
/*TODO*///	{
/*TODO*///		int i, j, start, stop;
/*TODO*///	
/*TODO*///		if (LOG_SHORTAGES != 0) logerror("----\n");
/*TODO*///	
/*TODO*///		/* reset the buffer */
/*TODO*///		memset(buffer, 0, length * sizeof(INT16));
/*TODO*///	
/*TODO*///		/* if we're redline racer, we have more DACs */
/*TODO*///		if (!is_redline)
/*TODO*///			start = 2, stop = 7;
/*TODO*///		else
/*TODO*///			start = 0, stop = 8;
/*TODO*///	
/*TODO*///		/* loop over manual DAC channels */
/*TODO*///		for (i = start; i < stop; i++)
/*TODO*///		{
/*TODO*///			struct dac_state *d = &dac[i];
/*TODO*///			int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///			/* if we have data, process it */
/*TODO*///			if (count > 0)
/*TODO*///			{
/*TODO*///				INT16 *base = d.buffer;
/*TODO*///				int source = d.bufout;
/*TODO*///				int frac = d.fraction;
/*TODO*///				int step = d.step;
/*TODO*///	
/*TODO*///				/* sample-rate convert to the output frequency */
/*TODO*///				for (j = 0; j < length && count > 0; j++)
/*TODO*///				{
/*TODO*///					buffer[j] += base[source];
/*TODO*///					frac += step;
/*TODO*///					source += frac >> 24;
/*TODO*///					count -= frac >> 24;
/*TODO*///					frac &= 0xffffff;
/*TODO*///					source &= DAC_BUFFER_SIZE_MASK;
/*TODO*///				}
/*TODO*///	
/*TODO*///				if (LOG_SHORTAGES && j < length)
/*TODO*///					logerror("DAC #%d short by %d/%d samples\n", i, length - j, length);
/*TODO*///	
/*TODO*///				/* update the DAC state */
/*TODO*///				d.fraction = frac;
/*TODO*///				d.bufout = source;
/*TODO*///			}
/*TODO*///	
/*TODO*///			/* update the clock status */
/*TODO*///			if (count < d.buftarget)
/*TODO*///			{
/*TODO*///				if (LOG_OPTIMIZATION != 0) logerror("  - trigger due to clock active in update\n");
/*TODO*///				cpu_trigger(CPU_RESUME_TRIGGER);
/*TODO*///				clock_active |= 1 << i;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	DMA-based DAC sound generation
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	static void leland_i186_dma_update(int param, INT16 *buffer, int length)
/*TODO*///	{
/*TODO*///		int i, j;
/*TODO*///	
/*TODO*///		/* reset the buffer */
/*TODO*///		memset(buffer, 0, length * sizeof(INT16));
/*TODO*///	
/*TODO*///		/* loop over DMA buffers */
/*TODO*///		for (i = 0; i < 2; i++)
/*TODO*///		{
/*TODO*///			struct dma_state *d = &i186.dma[i];
/*TODO*///	
/*TODO*///			/* check for enabled DMA */
/*TODO*///			if (d.control & 0x0002)
/*TODO*///			{
/*TODO*///				/* make sure the parameters meet our expectations */
/*TODO*///				if ((d.control & 0xfe00) != 0x1600)
/*TODO*///				{
/*TODO*///					logerror("Unexpected DMA control %02X\n", d.control);
/*TODO*///				}
/*TODO*///				else if (!is_redline && ((d.dest & 1) || (d.dest & 0x3f) > 0x0b))
/*TODO*///				{
/*TODO*///					logerror("Unexpected DMA destination %02X\n", d.dest);
/*TODO*///				}
/*TODO*///				else if (is_redline && (d.dest & 0xf000) != 0x4000 && (d.dest & 0xf000) != 0x5000)
/*TODO*///				{
/*TODO*///					logerror("Unexpected DMA destination %02X\n", d.dest);
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* otherwise, we're ready for liftoff */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					UINT8 *base = memory_region(REGION_CPU3);
/*TODO*///					int source = d.source;
/*TODO*///					int count = d.count;
/*TODO*///					int which, frac, step, volume;
/*TODO*///	
/*TODO*///					/* adjust for redline racer */
/*TODO*///					if (!is_redline)
/*TODO*///						which = (d.dest & 0x3f) / 2;
/*TODO*///					else
/*TODO*///						which = (d.dest >> 9) & 7;
/*TODO*///	
/*TODO*///					frac = dac[which].fraction;
/*TODO*///					step = dac[which].step;
/*TODO*///					volume = dac[which].volume;
/*TODO*///	
/*TODO*///					/* sample-rate convert to the output frequency */
/*TODO*///					for (j = 0; j < length && count > 0; j++)
/*TODO*///					{
/*TODO*///						buffer[j] += ((int)base[source] - 0x80) * volume;
/*TODO*///						frac += step;
/*TODO*///						source += frac >> 24;
/*TODO*///						count -= frac >> 24;
/*TODO*///						frac &= 0xffffff;
/*TODO*///					}
/*TODO*///	
/*TODO*///					/* update the DMA state */
/*TODO*///					if (count > 0)
/*TODO*///					{
/*TODO*///						d.source = source;
/*TODO*///						d.count = count;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						/* let the timer callback actually mark the transfer finished */
/*TODO*///						d.source = source + count - 1;
/*TODO*///						d.count = 1;
/*TODO*///						d.finished = 1;
/*TODO*///					}
/*TODO*///	
/*TODO*///					if (LOG_DMA != 0) logerror("DMA Generated %d samples - new count = %04X, source = %04X\n", j, d.count, d.source);
/*TODO*///	
/*TODO*///					/* update the DAC state */
/*TODO*///					dac[which].fraction = frac;
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
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
            /*TODO*///	
/*TODO*///		/* bail if nothing to play */
/*TODO*///		if (Machine.sample_rate == 0)
/*TODO*///			return 0;
/*TODO*///	
/*TODO*///		/* determine which sound hardware is installed */
/*TODO*///		has_ym2151 = 0;
/*TODO*///		for (i = 0; i < MAX_SOUND; i++)
/*TODO*///			if (Machine.drv.sound[i].sound_type == SOUND_YM2151)
/*TODO*///				has_ym2151 = 1;
/*TODO*///	
/*TODO*///		/* allocate separate streams for the DMA and non-DMA DACs */
/*TODO*///		dma_stream = stream_init("80186 DMA-driven DACs", 100, Machine.sample_rate, 0, leland_i186_dma_update);
/*TODO*///		nondma_stream = stream_init("80186 manually-driven DACs", 100, Machine.sample_rate, 0, leland_i186_dac_update);
/*TODO*///	
/*TODO*///		/* if we have a 2151, install an externally driven DAC stream */
/*TODO*///		if (has_ym2151 != 0)
/*TODO*///		{
/*TODO*///			ext_base = memory_region(REGION_SOUND1);
/*TODO*///			extern_stream = stream_init("80186 externally-driven DACs", 100, Machine.sample_rate, 0, leland_i186_extern_update);
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* by default, we're not redline racer */
/*TODO*///		is_redline = 0;
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

    /*TODO*///	
/*TODO*///	
/*TODO*///	static void leland_i186_reset(void)
/*TODO*///	{
/*TODO*///		/* kill any live timers */
/*TODO*///		if (i186.timer[0].int_timer) timer_remove(i186.timer[0].int_timer);
/*TODO*///		if (i186.timer[1].int_timer) timer_remove(i186.timer[1].int_timer);
/*TODO*///		if (i186.timer[2].int_timer) timer_remove(i186.timer[2].int_timer);
/*TODO*///		if (i186.timer[0].time_timer) timer_remove(i186.timer[0].time_timer);
/*TODO*///		if (i186.timer[1].time_timer) timer_remove(i186.timer[1].time_timer);
/*TODO*///		if (i186.timer[2].time_timer) timer_remove(i186.timer[2].time_timer);
/*TODO*///		if (i186.dma[0].finish_timer) timer_remove(i186.dma[0].finish_timer);
/*TODO*///		if (i186.dma[1].finish_timer) timer_remove(i186.dma[1].finish_timer);
/*TODO*///	
/*TODO*///		/* reset the i186 state */
/*TODO*///		memset(&i186, 0, sizeof(i186));
/*TODO*///	
/*TODO*///		/* reset the interrupt state */
/*TODO*///		i186.intr.priority_mask	= 0x0007;
/*TODO*///		i186.intr.timer 		= 0x000f;
/*TODO*///		i186.intr.dma[0]		= 0x000f;
/*TODO*///		i186.intr.dma[1]		= 0x000f;
/*TODO*///		i186.intr.ext[0]		= 0x000f;
/*TODO*///		i186.intr.ext[1]		= 0x000f;
/*TODO*///		i186.intr.ext[2]		= 0x000f;
/*TODO*///		i186.intr.ext[3]		= 0x000f;
/*TODO*///	
/*TODO*///		/* reset the DAC and counter states as well */
/*TODO*///		memset(&dac, 0, sizeof(dac));
/*TODO*///		memset(&counter, 0, sizeof(counter));
/*TODO*///	
/*TODO*///		/* send a trigger in case we're suspended */
/*TODO*///		if (LOG_OPTIMIZATION != 0) logerror("  - trigger due to reset\n");
/*TODO*///		cpu_trigger(CPU_RESUME_TRIGGER);
/*TODO*///		total_reads = 0;
/*TODO*///	
/*TODO*///		/* reset the sound systems */
/*TODO*///		sound_reset();
/*TODO*///	}
/*TODO*///	
/*TODO*///	
    public static void leland_i186_sound_init() {
        throw new UnsupportedOperationException("Unsupported");
        /*TODO*///		/* RAM is multiply mapped in the first 128k of address space */
/*TODO*///		cpu_setbank(6, ram_base);
/*TODO*///		cpu_setbank(7, ram_base);
/*TODO*///	
/*TODO*///		/* reset the I86 registers */
/*TODO*///		memset(&i186, 0, sizeof(i186));
/*TODO*///		leland_i186_reset();
/*TODO*///	
/*TODO*///		/* reset our internal stuff */
/*TODO*///		last_control = 0xf8;
/*TODO*///		clock_active = 0;
/*TODO*///	
/*TODO*///		/* reset the external DAC */
/*TODO*///		ext_start = 0;
/*TODO*///		ext_stop = 0;
/*TODO*///		ext_active = 0;
    }
    /*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	80186 interrupt controller
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	static int int_callback(int line)
/*TODO*///	{
/*TODO*///		if (LOG_INTERRUPTS != 0) logerror("(%f) **** Acknowledged interrupt vector %02X\n", timer_get_time(), i186.intr.poll_status & 0x1f);
/*TODO*///	
/*TODO*///		/* clear the interrupt */
/*TODO*///		i86_set_irq_line(0, CLEAR_LINE);
/*TODO*///		i186.intr.pending = 0;
/*TODO*///	
/*TODO*///		/* clear the request and set the in-service bit */
/*TODO*///	#if LATCH_INTS
/*TODO*///		i186.intr.request &= ~i186.intr.ack_mask;
/*TODO*///	#else
/*TODO*///		i186.intr.request &= ~(i186.intr.ack_mask & 0x0f);
/*TODO*///	#endif
/*TODO*///		i186.intr.in_service |= i186.intr.ack_mask;
/*TODO*///		if (i186.intr.ack_mask == 0x0001)
/*TODO*///		{
/*TODO*///			switch (i186.intr.poll_status & 0x1f)
/*TODO*///			{
/*TODO*///				case 0x08:	i186.intr.status &= ~0x01;	break;
/*TODO*///				case 0x12:	i186.intr.status &= ~0x02;	break;
/*TODO*///				case 0x13:	i186.intr.status &= ~0x04;	break;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		i186.intr.ack_mask = 0;
/*TODO*///	
/*TODO*///		/* a request no longer pending */
/*TODO*///		i186.intr.poll_status &= ~0x8000;
/*TODO*///	
/*TODO*///		/* return the vector */
/*TODO*///		return i186.intr.poll_status & 0x1f;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	static void update_interrupt_state(void)
/*TODO*///	{
/*TODO*///		int i, j, new_vector = 0;
/*TODO*///	
/*TODO*///		if (LOG_INTERRUPTS != 0) logerror("update_interrupt_status: req=%02X stat=%02X serv=%02X\n", i186.intr.request, i186.intr.status, i186.intr.in_service);
/*TODO*///	
/*TODO*///		/* loop over priorities */
/*TODO*///		for (i = 0; i <= i186.intr.priority_mask; i++)
/*TODO*///		{
/*TODO*///			/* note: by checking 4 bits, we also verify that the mask is off */
/*TODO*///			if ((i186.intr.timer & 15) == i)
/*TODO*///			{
/*TODO*///				/* if we're already servicing something at this level, don't generate anything new */
/*TODO*///				if (i186.intr.in_service & 0x01)
/*TODO*///					return;
/*TODO*///	
/*TODO*///				/* if there's something pending, generate an interrupt */
/*TODO*///				if (i186.intr.status & 0x07)
/*TODO*///				{
/*TODO*///					if (i186.intr.status & 1)
/*TODO*///						new_vector = 0x08;
/*TODO*///					else if (i186.intr.status & 2)
/*TODO*///						new_vector = 0x12;
/*TODO*///					else if (i186.intr.status & 4)
/*TODO*///						new_vector = 0x13;
/*TODO*///					else
/*TODO*///						usrintf_showmessage("Invalid timer interrupt!");
/*TODO*///	
/*TODO*///					/* set the clear mask and generate the int */
/*TODO*///					i186.intr.ack_mask = 0x0001;
/*TODO*///					goto generate_int;
/*TODO*///				}
/*TODO*///			}
/*TODO*///	
/*TODO*///			/* check DMA interrupts */
/*TODO*///			for (j = 0; j < 2; j++)
/*TODO*///				if ((i186.intr.dma[j] & 15) == i)
/*TODO*///				{
/*TODO*///					/* if we're already servicing something at this level, don't generate anything new */
/*TODO*///					if (i186.intr.in_service & (0x04 << j))
/*TODO*///						return;
/*TODO*///	
/*TODO*///					/* if there's something pending, generate an interrupt */
/*TODO*///					if (i186.intr.request & (0x04 << j))
/*TODO*///					{
/*TODO*///						new_vector = 0x0a + j;
/*TODO*///	
/*TODO*///						/* set the clear mask and generate the int */
/*TODO*///						i186.intr.ack_mask = 0x0004 << j;
/*TODO*///						goto generate_int;
/*TODO*///					}
/*TODO*///				}
/*TODO*///	
/*TODO*///			/* check external interrupts */
/*TODO*///			for (j = 0; j < 4; j++)
/*TODO*///				if ((i186.intr.ext[j] & 15) == i)
/*TODO*///				{
/*TODO*///					/* if we're already servicing something at this level, don't generate anything new */
/*TODO*///					if (i186.intr.in_service & (0x10 << j))
/*TODO*///						return;
/*TODO*///	
/*TODO*///					/* if there's something pending, generate an interrupt */
/*TODO*///					if (i186.intr.request & (0x10 << j))
/*TODO*///					{
/*TODO*///						/* otherwise, generate an interrupt for this request */
/*TODO*///						new_vector = 0x0c + j;
/*TODO*///	
/*TODO*///						/* set the clear mask and generate the int */
/*TODO*///						i186.intr.ack_mask = 0x0010 << j;
/*TODO*///						goto generate_int;
/*TODO*///					}
/*TODO*///				}
/*TODO*///		}
/*TODO*///		return;
/*TODO*///	
/*TODO*///	generate_int:
/*TODO*///		/* generate the appropriate interrupt */
/*TODO*///		i186.intr.poll_status = 0x8000 | new_vector;
/*TODO*///		if (!i186.intr.pending)
/*TODO*///			cpu_set_irq_line(2, 0, ASSERT_LINE);
/*TODO*///		i186.intr.pending = 1;
/*TODO*///		cpu_trigger(CPU_RESUME_TRIGGER);
/*TODO*///		if (LOG_OPTIMIZATION != 0) logerror("  - trigger due to interrupt pending\n");
/*TODO*///		if (LOG_INTERRUPTS != 0) logerror("(%f) **** Requesting interrupt vector %02X\n", timer_get_time(), new_vector);
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	static void handle_eoi(int data)
/*TODO*///	{
/*TODO*///		int i, j;
/*TODO*///	
/*TODO*///		/* specific case */
/*TODO*///		if (!(data & 0x8000))
/*TODO*///		{
/*TODO*///			/* turn off the appropriate in-service bit */
/*TODO*///			switch (data & 0x1f)
/*TODO*///			{
/*TODO*///				case 0x08:	i186.intr.in_service &= ~0x01;	break;
/*TODO*///				case 0x12:	i186.intr.in_service &= ~0x01;	break;
/*TODO*///				case 0x13:	i186.intr.in_service &= ~0x01;	break;
/*TODO*///				case 0x0a:	i186.intr.in_service &= ~0x04;	break;
/*TODO*///				case 0x0b:	i186.intr.in_service &= ~0x08;	break;
/*TODO*///				case 0x0c:	i186.intr.in_service &= ~0x10;	break;
/*TODO*///				case 0x0d:	i186.intr.in_service &= ~0x20;	break;
/*TODO*///				case 0x0e:	i186.intr.in_service &= ~0x40;	break;
/*TODO*///				case 0x0f:	i186.intr.in_service &= ~0x80;	break;
/*TODO*///				default:	logerror("%05X:ERROR - 80186 EOI with unknown vector %02X\n", cpu_get_pc(), data & 0x1f);
/*TODO*///			}
/*TODO*///			if (LOG_INTERRUPTS != 0) logerror("(%f) **** Got EOI for vector %02X\n", timer_get_time(), data & 0x1f);
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* non-specific case */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			/* loop over priorities */
/*TODO*///			for (i = 0; i <= 7; i++)
/*TODO*///			{
/*TODO*///				/* check for in-service timers */
/*TODO*///				if ((i186.intr.timer & 7) == i && (i186.intr.in_service & 0x01))
/*TODO*///				{
/*TODO*///					i186.intr.in_service &= ~0x01;
/*TODO*///					if (LOG_INTERRUPTS != 0) logerror("(%f) **** Got EOI for timer\n", timer_get_time());
/*TODO*///					return;
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* check for in-service DMA interrupts */
/*TODO*///				for (j = 0; j < 2; j++)
/*TODO*///					if ((i186.intr.dma[j] & 7) == i && (i186.intr.in_service & (0x04 << j)))
/*TODO*///					{
/*TODO*///						i186.intr.in_service &= ~(0x04 << j);
/*TODO*///						if (LOG_INTERRUPTS != 0) logerror("(%f) **** Got EOI for DMA%d\n", timer_get_time(), j);
/*TODO*///						return;
/*TODO*///					}
/*TODO*///	
/*TODO*///				/* check external interrupts */
/*TODO*///				for (j = 0; j < 4; j++)
/*TODO*///					if ((i186.intr.ext[j] & 7) == i && (i186.intr.in_service & (0x10 << j)))
/*TODO*///					{
/*TODO*///						i186.intr.in_service &= ~(0x10 << j);
/*TODO*///						if (LOG_INTERRUPTS != 0) logerror("(%f) **** Got EOI for INT%d\n", timer_get_time(), j);
/*TODO*///						return;
/*TODO*///					}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	80186 internal timers
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	static void internal_timer_int(int which)
/*TODO*///	{
/*TODO*///		struct timer_state *t = &i186.timer[which];
/*TODO*///	
/*TODO*///		if (LOG_TIMER != 0) logerror("Hit interrupt callback for timer %d\n", which);
/*TODO*///	
/*TODO*///		/* set the max count bit */
/*TODO*///		t.control |= 0x0020;
/*TODO*///	
/*TODO*///		/* request an interrupt */
/*TODO*///		if (t.control & 0x2000)
/*TODO*///		{
/*TODO*///			i186.intr.status |= 0x01 << which;
/*TODO*///			update_interrupt_state();
/*TODO*///			if (LOG_TIMER != 0) logerror("  Generating timer interrupt\n");
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* if we're continuous, reset */
/*TODO*///		if (t.control & 0x0001)
/*TODO*///		{
/*TODO*///			int count = t.maxA ? t.maxA : 0x10000;
/*TODO*///			t.int_timer = timer_set((double)count * TIME_IN_HZ(2000000), which, internal_timer_int);
/*TODO*///			if (LOG_TIMER != 0) logerror("  Repriming interrupt\n");
/*TODO*///		}
/*TODO*///		else
/*TODO*///			t.int_timer = NULL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	static void internal_timer_sync(int which)
/*TODO*///	{
/*TODO*///		struct timer_state *t = &i186.timer[which];
/*TODO*///	
/*TODO*///		/* if we have a timing timer running, adjust the count */
/*TODO*///		if (t.time_timer)
/*TODO*///		{
/*TODO*///			double current_time = timer_timeelapsed(t.time_timer);
/*TODO*///			int net_clocks = (int)((current_time - t.last_time) * 2000000.);
/*TODO*///			t.last_time = current_time;
/*TODO*///	
/*TODO*///			/* set the max count bit if we passed the max */
/*TODO*///			if ((int)t.count + net_clocks >= t.maxA)
/*TODO*///				t.control |= 0x0020;
/*TODO*///	
/*TODO*///			/* set the new count */
/*TODO*///			if (t.maxA != 0)
/*TODO*///				t.count = (t.count + net_clocks) % t.maxA;
/*TODO*///			else
/*TODO*///				t.count = t.count + net_clocks;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	static void internal_timer_update(int which, int new_count, int new_maxA, int new_maxB, int new_control)
/*TODO*///	{
/*TODO*///		struct timer_state *t = &i186.timer[which];
/*TODO*///		int update_int_timer = 0;
/*TODO*///	
/*TODO*///		/* if we have a new count and we're on, update things */
/*TODO*///		if (new_count != -1)
/*TODO*///		{
/*TODO*///			if (t.control & 0x8000)
/*TODO*///			{
/*TODO*///				internal_timer_sync(which);
/*TODO*///				update_int_timer = 1;
/*TODO*///			}
/*TODO*///			t.count = new_count;
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* if we have a new max and we're on, update things */
/*TODO*///		if (new_maxA != -1 && new_maxA != t.maxA)
/*TODO*///		{
/*TODO*///			if (t.control & 0x8000)
/*TODO*///			{
/*TODO*///				internal_timer_sync(which);
/*TODO*///				update_int_timer = 1;
/*TODO*///			}
/*TODO*///			t.maxA = new_maxA;
/*TODO*///			if (new_maxA == 0) new_maxA = 0x10000;
/*TODO*///	
/*TODO*///			/* redline racer controls nothing externally? */
/*TODO*///			if (is_redline != 0)
/*TODO*///				;
/*TODO*///	
/*TODO*///			/* on the common board, timer 0 controls the 10-bit DAC frequency */
/*TODO*///			else if (which == 0)
/*TODO*///				set_dac_frequency(6, 2000000 / new_maxA);
/*TODO*///	
/*TODO*///			/* timer 1 controls the externally driven DAC on Indy Heat/WSF */
/*TODO*///			else if (which == 1 && has_ym2151)
/*TODO*///				set_dac_frequency(7, 2000000 / (new_maxA * 2));
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* if we have a new max and we're on, update things */
/*TODO*///		if (new_maxB != -1 && new_maxB != t.maxB)
/*TODO*///		{
/*TODO*///			if (t.control & 0x8000)
/*TODO*///			{
/*TODO*///				internal_timer_sync(which);
/*TODO*///				update_int_timer = 1;
/*TODO*///			}
/*TODO*///			t.maxB = new_maxB;
/*TODO*///			if (new_maxB == 0) new_maxB = 0x10000;
/*TODO*///	
/*TODO*///			/* timer 1 controls the externally driven DAC on Indy Heat/WSF */
/*TODO*///			/* they alternate the use of maxA and maxB in a way that makes no */
/*TODO*///			/* sense according to the 80186 documentation! */
/*TODO*///			if (which == 1 && has_ym2151)
/*TODO*///				set_dac_frequency(7, 2000000 / (new_maxB * 2));
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* handle control changes */
/*TODO*///		if (new_control != -1)
/*TODO*///		{
/*TODO*///			int diff;
/*TODO*///	
/*TODO*///			/* merge back in the bits we don't modify */
/*TODO*///			new_control = (new_control & ~0x1fc0) | (t.control & 0x1fc0);
/*TODO*///	
/*TODO*///			/* handle the /INH bit */
/*TODO*///			if (!(new_control & 0x4000))
/*TODO*///				new_control = (new_control & ~0x8000) | (t.control & 0x8000);
/*TODO*///			new_control &= ~0x4000;
/*TODO*///	
/*TODO*///			/* check for control bits we don't handle */
/*TODO*///			diff = new_control ^ t.control;
/*TODO*///			if ((diff & 0x001c) != 0)
/*TODO*///				logerror("%05X:ERROR! - unsupported timer mode %04X\n", new_control);
/*TODO*///	
/*TODO*///			/* if we have real changes, update things */
/*TODO*///			if (diff != 0)
/*TODO*///			{
/*TODO*///				/* if we're going off, make sure our timers are gone */
/*TODO*///				if ((diff & 0x8000) && !(new_control & 0x8000))
/*TODO*///				{
/*TODO*///					/* compute the final count */
/*TODO*///					internal_timer_sync(which);
/*TODO*///	
/*TODO*///					/* nuke the timer and force the interrupt timer to be recomputed */
/*TODO*///					if (t.time_timer)
/*TODO*///						timer_remove(t.time_timer);
/*TODO*///					t.time_timer = NULL;
/*TODO*///					update_int_timer = 1;
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* if we're going on, start the timers running */
/*TODO*///				else if ((diff & 0x8000) && (new_control & 0x8000))
/*TODO*///				{
/*TODO*///					/* start the timing */
/*TODO*///					t.time_timer = timer_set(TIME_NEVER, 0, NULL);
/*TODO*///					update_int_timer = 1;
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* if something about the interrupt timer changed, force an update */
/*TODO*///				if (!(diff & 0x8000) && (diff & 0x2000))
/*TODO*///				{
/*TODO*///					internal_timer_sync(which);
/*TODO*///					update_int_timer = 1;
/*TODO*///				}
/*TODO*///			}
/*TODO*///	
/*TODO*///			/* set the new control register */
/*TODO*///			t.control = new_control;
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* update the interrupt timer */
/*TODO*///	
/*TODO*///		/* kludge: the YM2151 games sometimes crank timer 1 really high, and leave interrupts */
/*TODO*///		/* enabled, even though the handler for timer 1 does nothing. To alleviate this, we */
/*TODO*///		/* just ignore it */
/*TODO*///		if (!has_ym2151 || which != 1)
/*TODO*///			if (update_int_timer != 0)
/*TODO*///			{
/*TODO*///				if (t.int_timer)
/*TODO*///					timer_remove(t.int_timer);
/*TODO*///				if ((t.control & 0x8000) && (t.control & 0x2000))
/*TODO*///				{
/*TODO*///					int diff = t.maxA - t.count;
/*TODO*///					if (diff <= 0) diff += 0x10000;
/*TODO*///					t.int_timer = timer_set((double)diff * TIME_IN_HZ(2000000), which, internal_timer_int);
/*TODO*///					if (LOG_TIMER != 0) logerror("Set interrupt timer for %d\n", which);
/*TODO*///				}
/*TODO*///				else
/*TODO*///					t.int_timer = NULL;
/*TODO*///			}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	80186 internal DMA
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	static void dma_timer_callback(int which)
/*TODO*///	{
/*TODO*///		struct dma_state *d = &i186.dma[which];
/*TODO*///	
/*TODO*///		/* force an update and see if we're really done */
/*TODO*///		stream_update(dma_stream, 0);
/*TODO*///	
/*TODO*///		/* complete the status update */
/*TODO*///		d.control &= ~0x0002;
/*TODO*///		d.source += d.count;
/*TODO*///		d.count = 0;
/*TODO*///	
/*TODO*///		/* check for interrupt generation */
/*TODO*///		if (d.control & 0x0100)
/*TODO*///		{
/*TODO*///			if (LOG_DMA != 0) logerror("DMA%d timer callback - requesting interrupt: count = %04X, source = %04X\n", which, d.count, d.source);
/*TODO*///			i186.intr.request |= 0x04 << which;
/*TODO*///			update_interrupt_state();
/*TODO*///		}
/*TODO*///		d.finish_timer = NULL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	static void update_dma_control(int which, int new_control)
/*TODO*///	{
/*TODO*///		struct dma_state *d = &i186.dma[which];
/*TODO*///		int diff;
/*TODO*///	
/*TODO*///		/* handle the CHG bit */
/*TODO*///		if (!(new_control & 0x0004))
/*TODO*///			new_control = (new_control & ~0x0002) | (d.control & 0x0002);
/*TODO*///		new_control &= ~0x0004;
/*TODO*///	
/*TODO*///		/* check for control bits we don't handle */
/*TODO*///		diff = new_control ^ d.control;
/*TODO*///		if ((diff & 0x6811) != 0)
/*TODO*///			logerror("%05X:ERROR! - unsupported DMA mode %04X\n", new_control);
/*TODO*///	
/*TODO*///		/* if we're going live, set a timer */
/*TODO*///		if ((diff & 0x0002) && (new_control & 0x0002))
/*TODO*///		{
/*TODO*///			/* make sure the parameters meet our expectations */
/*TODO*///			if ((new_control & 0xfe00) != 0x1600)
/*TODO*///			{
/*TODO*///				logerror("Unexpected DMA control %02X\n", new_control);
/*TODO*///			}
/*TODO*///			else if (!is_redline && ((d.dest & 1) || (d.dest & 0x3f) > 0x0b))
/*TODO*///			{
/*TODO*///				logerror("Unexpected DMA destination %02X\n", d.dest);
/*TODO*///			}
/*TODO*///			else if (is_redline && (d.dest & 0xf000) != 0x4000 && (d.dest & 0xf000) != 0x5000)
/*TODO*///			{
/*TODO*///				logerror("Unexpected DMA destination %02X\n", d.dest);
/*TODO*///			}
/*TODO*///	
/*TODO*///			/* otherwise, set a timer */
/*TODO*///			else
/*TODO*///			{
/*TODO*///				int count = d.count;
/*TODO*///				int dacnum;
/*TODO*///	
/*TODO*///				/* adjust for redline racer */
/*TODO*///				if (!is_redline)
/*TODO*///					dacnum = (d.dest & 0x3f) / 2;
/*TODO*///				else
/*TODO*///				{
/*TODO*///					dacnum = (d.dest >> 9) & 7;
/*TODO*///					dac[dacnum].volume = (d.dest & 0x1fe) / 2 / DAC_VOLUME_SCALE;
/*TODO*///				}
/*TODO*///	
/*TODO*///				if (LOG_DMA != 0) logerror("Initiated DMA %d - count = %04X, source = %04X, dest = %04X\n", which, d.count, d.source, d.dest);
/*TODO*///	
/*TODO*///				if (d.finish_timer)
/*TODO*///					timer_remove(d.finish_timer);
/*TODO*///				d.finished = 0;
/*TODO*///				d.finish_timer = timer_set(TIME_IN_HZ(dac[dacnum].frequency) * (double)count, which, dma_timer_callback);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* set the new control register */
/*TODO*///		d.control = new_control;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
	
	/*************************************
	 *
	 *	80186 internal I/O reads
	 *
	 *************************************/
	
	public static ReadHandlerPtr i186_internal_port_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int shift = 8 * (offset & 1);
		int temp, which;
	
		switch (offset & ~1)
		{
			case 0x22:
				logerror("%05X:ERROR - read from 80186 EOI\n", cpu_get_pc());
				break;
/*TODO*///	
/*TODO*///			case 0x24:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 interrupt poll\n", cpu_get_pc());
/*TODO*///				if (i186.intr.poll_status & 0x8000)
/*TODO*///					int_callback(0);
/*TODO*///				return (i186.intr.poll_status >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x26:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 interrupt poll status\n", cpu_get_pc());
/*TODO*///				return (i186.intr.poll_status >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x28:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 interrupt mask\n", cpu_get_pc());
/*TODO*///				temp  = (i186.intr.timer  >> 3) & 0x01;
/*TODO*///				temp |= (i186.intr.dma[0] >> 1) & 0x04;
/*TODO*///				temp |= (i186.intr.dma[1] >> 0) & 0x08;
/*TODO*///				temp |= (i186.intr.ext[0] << 1) & 0x10;
/*TODO*///				temp |= (i186.intr.ext[1] << 2) & 0x20;
/*TODO*///				temp |= (i186.intr.ext[2] << 3) & 0x40;
/*TODO*///				temp |= (i186.intr.ext[3] << 4) & 0x80;
/*TODO*///				return (temp >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x2a:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 interrupt priority mask\n", cpu_get_pc());
/*TODO*///				return (i186.intr.priority_mask >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x2c:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 interrupt in-service\n", cpu_get_pc());
/*TODO*///				return (i186.intr.in_service >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x2e:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 interrupt request\n", cpu_get_pc());
/*TODO*///				temp = i186.intr.request & ~0x0001;
/*TODO*///				if (i186.intr.status & 0x0007)
/*TODO*///					temp |= 1;
/*TODO*///				return (temp >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x30:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 interrupt status\n", cpu_get_pc());
/*TODO*///				return (i186.intr.status >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x32:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 timer interrupt control\n", cpu_get_pc());
/*TODO*///				return (i186.intr.timer >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x34:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA 0 interrupt control\n", cpu_get_pc());
/*TODO*///				return (i186.intr.dma[0] >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x36:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA 1 interrupt control\n", cpu_get_pc());
/*TODO*///				return (i186.intr.dma[1] >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x38:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 INT 0 interrupt control\n", cpu_get_pc());
/*TODO*///				return (i186.intr.ext[0] >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x3a:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 INT 1 interrupt control\n", cpu_get_pc());
/*TODO*///				return (i186.intr.ext[1] >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x3c:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 INT 2 interrupt control\n", cpu_get_pc());
/*TODO*///				return (i186.intr.ext[2] >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x3e:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 INT 3 interrupt control\n", cpu_get_pc());
/*TODO*///				return (i186.intr.ext[3] >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x50:
/*TODO*///			case 0x58:
/*TODO*///			case 0x60:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 Timer %d count\n", cpu_get_pc(), (offset - 0x50) / 8);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				if (!(offset & 1))
/*TODO*///					internal_timer_sync(which);
/*TODO*///				return (i186.timer[which].count >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x52:
/*TODO*///			case 0x5a:
/*TODO*///			case 0x62:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 Timer %d max A\n", cpu_get_pc(), (offset - 0x50) / 8);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				return (i186.timer[which].maxA >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x54:
/*TODO*///			case 0x5c:
/*TODO*///				logerror("%05X:read 80186 Timer %d max B\n", cpu_get_pc(), (offset - 0x50) / 8);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				return (i186.timer[which].maxB >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0x56:
/*TODO*///			case 0x5e:
/*TODO*///			case 0x66:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 Timer %d control\n", cpu_get_pc(), (offset - 0x50) / 8);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				return (i186.timer[which].control >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xa0:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 upper chip select\n", cpu_get_pc());
/*TODO*///				return (i186.mem.upper >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xa2:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 lower chip select\n", cpu_get_pc());
/*TODO*///				return (i186.mem.lower >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xa4:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 peripheral chip select\n", cpu_get_pc());
/*TODO*///				return (i186.mem.peripheral >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xa6:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 middle chip select\n", cpu_get_pc());
/*TODO*///				return (i186.mem.middle >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xa8:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 middle P chip select\n", cpu_get_pc());
/*TODO*///				return (i186.mem.middle_size >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xc0:
/*TODO*///			case 0xd0:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA%d lower source address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				return (i186.dma[which].source >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xc2:
/*TODO*///			case 0xd2:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA%d upper source address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				return (i186.dma[which].source >> (shift + 16)) & 0xff;
/*TODO*///	
/*TODO*///			case 0xc4:
/*TODO*///			case 0xd4:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA%d lower dest address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				return (i186.dma[which].dest >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xc6:
/*TODO*///			case 0xd6:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA%d upper dest address\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				return (i186.dma[which].dest >> (shift + 16)) & 0xff;
/*TODO*///	
/*TODO*///			case 0xc8:
/*TODO*///			case 0xd8:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA%d transfer count\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				return (i186.dma[which].count >> shift) & 0xff;
/*TODO*///	
/*TODO*///			case 0xca:
/*TODO*///			case 0xda:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:read 80186 DMA%d control\n", cpu_get_pc(), (offset - 0xc0) / 0x10);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				return (i186.dma[which].control >> shift) & 0xff;
/*TODO*///	
			default:
				logerror("%05X:read 80186 port %02X\n", cpu_get_pc(), offset);
				break;
		}
		return 0x00;
	} };
/*TODO*///	
/*TODO*///	
	
	/*************************************
	 *
	 *	80186 internal I/O writes
	 *
	 *************************************/
	
	public static WriteHandlerPtr i186_internal_port_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
/*TODO*///		static UINT8 even_byte;
/*TODO*///		int temp, which;
/*TODO*///	
/*TODO*///		/* warning: this assumes all port writes here are word-sized */
/*TODO*///		if (!(offset & 1))
/*TODO*///		{
/*TODO*///			even_byte = data;
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		data = ((data & 0xff) << 8) | even_byte;
/*TODO*///	
/*TODO*///		switch (offset & ~1)
/*TODO*///		{
/*TODO*///			case 0x22:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 EOI = %04X\n", cpu_get_pc(), data);
/*TODO*///				handle_eoi(0x8000);
/*TODO*///				update_interrupt_state();
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x24:
/*TODO*///				logerror("%05X:ERROR - write to 80186 interrupt poll = %04X\n", cpu_get_pc(), data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x26:
/*TODO*///				logerror("%05X:ERROR - write to 80186 interrupt poll status = %04X\n", cpu_get_pc(), data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x28:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 interrupt mask = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.timer  = (i186.intr.timer  & ~0x08) | ((data << 3) & 0x08);
/*TODO*///				i186.intr.dma[0] = (i186.intr.dma[0] & ~0x08) | ((data << 1) & 0x08);
/*TODO*///				i186.intr.dma[1] = (i186.intr.dma[1] & ~0x08) | ((data << 0) & 0x08);
/*TODO*///				i186.intr.ext[0] = (i186.intr.ext[0] & ~0x08) | ((data >> 1) & 0x08);
/*TODO*///				i186.intr.ext[1] = (i186.intr.ext[1] & ~0x08) | ((data >> 2) & 0x08);
/*TODO*///				i186.intr.ext[2] = (i186.intr.ext[2] & ~0x08) | ((data >> 3) & 0x08);
/*TODO*///				i186.intr.ext[3] = (i186.intr.ext[3] & ~0x08) | ((data >> 4) & 0x08);
/*TODO*///				update_interrupt_state();
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x2a:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 interrupt priority mask = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.priority_mask = data & 0x0007;
/*TODO*///				update_interrupt_state();
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x2c:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 interrupt in-service = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.in_service = data & 0x00ff;
/*TODO*///				update_interrupt_state();
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x2e:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 interrupt request = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.request = (i186.intr.request & ~0x00c0) | (data & 0x00c0);
/*TODO*///				update_interrupt_state();
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x30:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:WARNING - wrote to 80186 interrupt status = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.status = (i186.intr.status & ~0x8007) | (data & 0x8007);
/*TODO*///				update_interrupt_state();
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x32:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 timer interrupt contol = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.timer = data & 0x000f;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x34:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA 0 interrupt control = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.dma[0] = data & 0x000f;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x36:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA 1 interrupt control = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.dma[1] = data & 0x000f;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x38:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 INT 0 interrupt control = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.ext[0] = data & 0x007f;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x3a:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 INT 1 interrupt control = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.ext[1] = data & 0x007f;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x3c:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 INT 2 interrupt control = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.ext[2] = data & 0x001f;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x3e:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 INT 3 interrupt control = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.intr.ext[3] = data & 0x001f;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x50:
/*TODO*///			case 0x58:
/*TODO*///			case 0x60:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 Timer %d count = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				internal_timer_update(which, data, -1, -1, -1);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x52:
/*TODO*///			case 0x5a:
/*TODO*///			case 0x62:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 Timer %d max A = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				internal_timer_update(which, -1, data, -1, -1);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x54:
/*TODO*///			case 0x5c:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 Timer %d max B = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				internal_timer_update(which, -1, -1, data, -1);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0x56:
/*TODO*///			case 0x5e:
/*TODO*///			case 0x66:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 Timer %d control = %04X\n", cpu_get_pc(), (offset - 0x50) / 8, data);
/*TODO*///				which = (offset - 0x50) / 8;
/*TODO*///				internal_timer_update(which, -1, -1, -1, data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xa0:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 upper chip select = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.mem.upper = data | 0xc038;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xa2:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 lower chip select = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.mem.lower = (data & 0x3fff) | 0x0038;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xa4:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 peripheral chip select = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.mem.peripheral = data | 0x0038;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xa6:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 middle chip select = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.mem.middle = data | 0x01f8;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xa8:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 middle P chip select = %04X\n", cpu_get_pc(), data);
/*TODO*///				i186.mem.middle_size = data | 0x8038;
/*TODO*///	
/*TODO*///				temp = (i186.mem.peripheral & 0xffc0) << 4;
/*TODO*///				if (i186.mem.middle_size & 0x0040)
/*TODO*///				{
/*TODO*///					install_mem_read_handler(2, temp, temp + 0x2ff, peripheral_r);
/*TODO*///					install_mem_write_handler(2, temp, temp + 0x2ff, peripheral_w);
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					temp &= 0xffff;
/*TODO*///					install_port_read_handler(2, temp, temp + 0x2ff, peripheral_r);
/*TODO*///					install_port_write_handler(2, temp, temp + 0x2ff, peripheral_w);
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* we need to do this at a time when the I86 context is swapped in */
/*TODO*///				/* this register is generally set once at startup and never again, so it's a good */
/*TODO*///				/* time to set it up */
/*TODO*///				i86_set_irq_callback(int_callback);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xc0:
/*TODO*///			case 0xd0:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA%d lower source address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				i186.dma[which].source = (i186.dma[which].source & ~0x0ffff) | (data & 0x0ffff);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xc2:
/*TODO*///			case 0xd2:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA%d upper source address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				i186.dma[which].source = (i186.dma[which].source & ~0xf0000) | ((data << 16) & 0xf0000);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xc4:
/*TODO*///			case 0xd4:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA%d lower dest address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				i186.dma[which].dest = (i186.dma[which].dest & ~0x0ffff) | (data & 0x0ffff);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xc6:
/*TODO*///			case 0xd6:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA%d upper dest address = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				i186.dma[which].dest = (i186.dma[which].dest & ~0xf0000) | ((data << 16) & 0xf0000);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xc8:
/*TODO*///			case 0xd8:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA%d transfer count = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				i186.dma[which].count = data;
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xca:
/*TODO*///			case 0xda:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 DMA%d control = %04X\n", cpu_get_pc(), (offset - 0xc0) / 0x10, data);
/*TODO*///				which = (offset - 0xc0) / 0x10;
/*TODO*///				stream_update(dma_stream, 0);
/*TODO*///				update_dma_control(which, data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 0xfe:
/*TODO*///				if (LOG_PORTS != 0) logerror("%05X:80186 relocation register = %04X\n", cpu_get_pc(), data);
/*TODO*///	
/*TODO*///				/* we assume here there that this doesn't happen too often */
/*TODO*///				/* plus, we can't really remove the old memory range, so we also assume that it's */
/*TODO*///				/* okay to leave us mapped where we were */
/*TODO*///				temp = (data & 0x0fff) << 8;
/*TODO*///				if ((data & 0x1000) != 0)
/*TODO*///				{
/*TODO*///					install_mem_read_handler(2, temp, temp + 0xff, i186_internal_port_r);
/*TODO*///					install_mem_write_handler(2, temp, temp + 0xff, i186_internal_port_w);
/*TODO*///				}
/*TODO*///				else
/*TODO*///				{
/*TODO*///					temp &= 0xffff;
/*TODO*///					install_port_read_handler(2, temp, temp + 0xff, i186_internal_port_r);
/*TODO*///					install_port_write_handler(2, temp, temp + 0xff, i186_internal_port_w);
/*TODO*///				}
/*TODO*///	/*			usrintf_showmessage("Sound CPU reset");*/
/*TODO*///				break;
/*TODO*///	
/*TODO*///			default:
/*TODO*///				logerror("%05X:80186 port %02X = %04X\n", cpu_get_pc(), offset, data);
/*TODO*///				break;
/*TODO*///		}
	} };
	
	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	8254 PIT accesses
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	INLINE void counter_update_count(int which)
/*TODO*///	{
/*TODO*///		/* only update if the timer is running */
/*TODO*///		if (counter[which].timer)
/*TODO*///		{
/*TODO*///			/* determine how many 2MHz cycles are remaining */
/*TODO*///			int count = (int)(timer_timeleft(counter[which].timer) / TIME_IN_HZ(2000000));
/*TODO*///			counter[which].count = (count < 0) ? 0 : count;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	public static ReadHandlerPtr pit8254_r  = new ReadHandlerPtr() { public int handler(int offset)
/*TODO*///	{
/*TODO*///		struct counter_state *ctr;
/*TODO*///		int which = offset / 0x80;
/*TODO*///		int reg = (offset / 2) & 3;
/*TODO*///	
/*TODO*///		/* ignore odd offsets */
/*TODO*///		if ((offset & 1) != 0)
/*TODO*///			return 0;
/*TODO*///	
/*TODO*///		/* switch off the register */
/*TODO*///		switch (offset & 3)
/*TODO*///		{
/*TODO*///			case 0:
/*TODO*///			case 1:
/*TODO*///			case 2:
/*TODO*///				/* warning: assumes LSB/MSB addressing and no latching! */
/*TODO*///				which = (which * 3) + reg;
/*TODO*///				ctr = &counter[which];
/*TODO*///	
/*TODO*///				/* update the count */
/*TODO*///				counter_update_count(which);
/*TODO*///	
/*TODO*///				/* return the LSB */
/*TODO*///				if (counter[which].readbyte == 0)
/*TODO*///				{
/*TODO*///					counter[which].readbyte = 1;
/*TODO*///					return counter[which].count & 0xff;
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* write the MSB and reset the counter */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					counter[which].readbyte = 0;
/*TODO*///					return (counter[which].count >> 8) & 0xff;
/*TODO*///				}
/*TODO*///				break;
/*TODO*///		}
/*TODO*///		return 0;
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	public static WriteHandlerPtr pit8254_w = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
/*TODO*///		struct counter_state *ctr;
/*TODO*///		int which = offset / 0x80;
/*TODO*///		int reg = (offset / 2) & 3;
/*TODO*///	
/*TODO*///		/* ignore odd offsets */
/*TODO*///		if ((offset & 1) != 0)
/*TODO*///			return;
/*TODO*///	
/*TODO*///		/* switch off the register */
/*TODO*///		switch (reg)
/*TODO*///		{
/*TODO*///			case 0:
/*TODO*///			case 1:
/*TODO*///			case 2:
/*TODO*///				/* warning: assumes LSB/MSB addressing and no latching! */
/*TODO*///				which = (which * 3) + reg;
/*TODO*///				ctr = &counter[which];
/*TODO*///	
/*TODO*///				/* write the LSB */
/*TODO*///				if (ctr.writebyte == 0)
/*TODO*///				{
/*TODO*///					ctr.count = (ctr.count & 0xff00) | (data & 0x00ff);
/*TODO*///					ctr.writebyte = 1;
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* write the MSB and reset the counter */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					ctr.count = (ctr.count & 0x00ff) | ((data << 8) & 0xff00);
/*TODO*///					ctr.writebyte = 0;
/*TODO*///	
/*TODO*///					/* treat 0 as $10000 */
/*TODO*///					if (ctr.count == 0) ctr.count = 0x10000;
/*TODO*///	
/*TODO*///					/* reset/start the timer */
/*TODO*///					if (ctr.timer)
/*TODO*///						timer_reset(ctr.timer, TIME_NEVER);
/*TODO*///					else
/*TODO*///						ctr.timer = timer_set(TIME_NEVER, 0, NULL);
/*TODO*///	
/*TODO*///					if (LOG_PIT != 0) logerror("PIT counter %d set to %d (%d Hz)\n", which, ctr.count, 4000000 / ctr.count);
/*TODO*///	
/*TODO*///					/* set the frequency of the associated DAC */
/*TODO*///					if (!is_redline)
/*TODO*///						set_dac_frequency(which, 4000000 / ctr.count);
/*TODO*///					else
/*TODO*///					{
/*TODO*///						if (which < 5)
/*TODO*///							set_dac_frequency(which, 7000000 / ctr.count);
/*TODO*///						else if (which == 6)
/*TODO*///						{
/*TODO*///							set_dac_frequency(5, 7000000 / ctr.count);
/*TODO*///							set_dac_frequency(6, 7000000 / ctr.count);
/*TODO*///							set_dac_frequency(7, 7000000 / ctr.count);
/*TODO*///						}
/*TODO*///					}
/*TODO*///				}
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 3:
/*TODO*///				/* determine which counter */
/*TODO*///				if ((data & 0xc0) == 0xc0) break;
/*TODO*///				which = (which * 3) + (data >> 6);
/*TODO*///				ctr = &counter[which];
/*TODO*///	
/*TODO*///				/* set the mode */
/*TODO*///				ctr.mode = (data >> 1) & 7;
/*TODO*///				break;
/*TODO*///		}
/*TODO*///	} };
/*TODO*///	
/*TODO*///	

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
            throw new UnsupportedOperationException("Unsupported");
            /*TODO*///		last_control = data;
/*TODO*///	
/*TODO*///		if (LOG_COMM != 0)
/*TODO*///		{
/*TODO*///			logerror("%04X:I86 control = %02X", cpu_getpreviouspc(), data);
/*TODO*///			if (!(data & 0x80)) logerror("  /RESET");
/*TODO*///			if (!(data & 0x40)) logerror("  ZNMI");
/*TODO*///			if (!(data & 0x20)) logerror("  INT0");
/*TODO*///			if (!(data & 0x10)) logerror("  /TEST");
/*TODO*///			if (!(data & 0x08)) logerror("  INT1");
/*TODO*///			logerror("\n");
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* /RESET */
/*TODO*///		cpu_set_reset_line(2, data & 0x80  ? CLEAR_LINE : ASSERT_LINE);
/*TODO*///	
/*TODO*///		/* /NMI */
/*TODO*///	/* 	If the master CPU doesn't get a response by the time it's ready to send
/*TODO*///		the next command, it uses an NMI to force the issue; unfortunately, this
/*TODO*///		seems to really screw up the sound system. It turns out it's better to
/*TODO*///		just wait for the original interrupt to occur naturally */
/*TODO*///	/*	cpu_set_nmi_line  (2, data & 0x40  ? CLEAR_LINE : ASSERT_LINE);*/
/*TODO*///	
/*TODO*///		/* INT0 */
/*TODO*///		if ((data & 0x20) != 0)
/*TODO*///		{
/*TODO*///			if (!LATCH_INTS) i186.intr.request &= ~0x10;
/*TODO*///		}
/*TODO*///		else if (i186.intr.ext[0] & 0x10)
/*TODO*///			i186.intr.request |= 0x10;
/*TODO*///		else if ((diff & 0x20) != 0)
/*TODO*///			i186.intr.request |= 0x10;
/*TODO*///	
/*TODO*///		/* INT1 */
/*TODO*///		if ((data & 0x08) != 0)
/*TODO*///		{
/*TODO*///			if (!LATCH_INTS) i186.intr.request &= ~0x20;
/*TODO*///		}
/*TODO*///		else if (i186.intr.ext[1] & 0x10)
/*TODO*///			i186.intr.request |= 0x20;
/*TODO*///		else if ((diff & 0x08) != 0)
/*TODO*///			i186.intr.request |= 0x20;
/*TODO*///	
/*TODO*///		/* handle reset here */
/*TODO*///		if ((diff & 0x80) && (data & 0x80))
/*TODO*///			leland_i186_reset();
/*TODO*///	
/*TODO*///		update_interrupt_state();
        }
    };

    /**
     * ***********************************
     *
     * Sound command handling
     *
     ************************************
     */
    public static timer_callback command_lo_sync
            = new timer_callback() {
        public void handler(int data) {
            //if (LOG_COMM != 0) logerror("%04X:Write sound command latch lo = %02X\n", cpu_getpreviouspc(), data);
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
            //if (LOG_COMM != 0) logerror("%04X:Write sound command latch hi = %02X\n", cpu_getpreviouspc(), data);
            u8_sound_command[1] = data & 0xFF;
        }
    };

    /*TODO*///	
/*TODO*///	public static ReadHandlerPtr main_to_sound_comm_r  = new ReadHandlerPtr() { public int handler(int offset)
/*TODO*///	{
/*TODO*///		if (!(offset & 1))
/*TODO*///		{
/*TODO*///			if (LOG_COMM != 0) logerror("%05X:Read sound command latch lo = %02X\n", cpu_get_pc(), sound_command[0]);
/*TODO*///			return sound_command[0];
/*TODO*///		}
/*TODO*///		else
/*TODO*///		{
/*TODO*///			if (LOG_COMM != 0) logerror("%05X:Read sound command latch hi = %02X\n", cpu_get_pc(), sound_command[1]);
/*TODO*///			return sound_command[1];
/*TODO*///		}
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Sound response handling
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
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
                //if (LOG_COMM != 0) logerror("(Updated sound response latch to %02X)\n", sound_response);
                oldaf = (oldaf & 0x00ff) | (sound_response << 8);
                cpunum_set_reg(0, Z80_AF, oldaf);
            } else {
                logerror("ERROR: delayed_response_r - current PC = %04X, checkPC = %04X\n", pc, checkpc);
            }
        }
    };

    public static ReadHandlerPtr leland_i86_response_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            //if (LOG_COMM != 0) logerror("%04X:Read sound response latch = %02X\n", cpu_getpreviouspc(), sound_response);

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
    /*TODO*///	
/*TODO*///	
/*TODO*///	public static WriteHandlerPtr sound_to_main_comm_w = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
/*TODO*///		if (LOG_COMM != 0) logerror("%05X:Write sound response latch = %02X\n", cpu_get_pc(), data);
/*TODO*///		sound_response = data;
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Low-level DAC I/O
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	static void set_dac_frequency(int which, int frequency)
/*TODO*///	{
/*TODO*///		struct dac_state *d = &dac[which];
/*TODO*///		int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///		/* set the frequency of the associated DAC */
/*TODO*///		d.frequency = frequency;
/*TODO*///		d.step = (int)((double)frequency * (double)(1 << 24) / (double)Machine.sample_rate);
/*TODO*///	
/*TODO*///		/* also determine the target buffer size */
/*TODO*///		d.buftarget = dac[which].frequency / 60 + 50;
/*TODO*///		if (d.buftarget > DAC_BUFFER_SIZE - 1)
/*TODO*///			d.buftarget = DAC_BUFFER_SIZE - 1;
/*TODO*///	
/*TODO*///		/* reevaluate the count */
/*TODO*///		if (count > d.buftarget)
/*TODO*///			clock_active &= ~(1 << which);
/*TODO*///		else if (count < d.buftarget)
/*TODO*///		{
/*TODO*///			if (LOG_OPTIMIZATION != 0) logerror("  - trigger due to clock active in set_dac_frequency\n");
/*TODO*///			cpu_trigger(CPU_RESUME_TRIGGER);
/*TODO*///			clock_active |= 1 << which;
/*TODO*///		}
/*TODO*///	
/*TODO*///		if (LOG_DAC != 0) logerror("DAC %d frequency = %d, step = %08X\n", which, d.frequency, d.step);
/*TODO*///	}
/*TODO*///	
	
	public static WriteHandlerPtr dac_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
/*TODO*///		int which = offset / 2;
/*TODO*///		struct dac_state *d = &dac[which];
/*TODO*///	
/*TODO*///		/* handle value changes */
/*TODO*///		if (!(offset & 1))
/*TODO*///		{
/*TODO*///			int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///			/* set the new value */
/*TODO*///			d.value = (INT16)data - 0x80;
/*TODO*///			if (LOG_DAC != 0) logerror("%05X:DAC %d value = %02X\n", cpu_get_pc(), offset / 2, data);
/*TODO*///	
/*TODO*///			/* if we haven't overflowed the buffer, add the value value to it */
/*TODO*///			if (count < DAC_BUFFER_SIZE - 1)
/*TODO*///			{
/*TODO*///				/* if this is the first byte, sync the stream */
/*TODO*///				if (count == 0)
/*TODO*///					stream_update(nondma_stream, 0);
/*TODO*///	
/*TODO*///				/* prescale by the volume */
/*TODO*///				d.buffer[d.bufin] = d.value * d.volume;
/*TODO*///				d.bufin = (d.bufin + 1) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///				/* update the clock status */
/*TODO*///				if (++count > d.buftarget)
/*TODO*///					clock_active &= ~(1 << which);
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* handle volume changes */
/*TODO*///		else
/*TODO*///		{
/*TODO*///			d.volume = (data ^ 0x00) / DAC_VOLUME_SCALE;
/*TODO*///			if (LOG_DAC != 0) logerror("%05X:DAC %d volume = %02X\n", cpu_get_pc(), offset / 2, data);
/*TODO*///		}
	} };
	
	
	public static WriteHandlerPtr redline_dac_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
/*TODO*///		int which = offset / 0x200;
/*TODO*///		struct dac_state *d = &dac[which];
/*TODO*///		int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///		/* set the new value */
/*TODO*///		d.value = (INT16)data - 0x80;
/*TODO*///	
/*TODO*///		/* if we haven't overflowed the buffer, add the value value to it */
/*TODO*///		if (count < DAC_BUFFER_SIZE - 1)
/*TODO*///		{
/*TODO*///			/* if this is the first byte, sync the stream */
/*TODO*///			if (count == 0)
/*TODO*///				stream_update(nondma_stream, 0);
/*TODO*///	
/*TODO*///			/* prescale by the volume */
/*TODO*///			d.buffer[d.bufin] = d.value * d.volume;
/*TODO*///			d.bufin = (d.bufin + 1) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///			/* update the clock status */
/*TODO*///			if (++count > d.buftarget)
/*TODO*///				clock_active &= ~(1 << which);
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* update the volume */
/*TODO*///		d.volume = (offset & 0x1fe) / 2 / DAC_VOLUME_SCALE;
/*TODO*///		if (LOG_DAC != 0) logerror("%05X:DAC %d value = %02X, volume = %02X\n", cpu_get_pc(), which, data, (offset & 0x1fe) / 2);
	} };
	
	
/*TODO*///	public static WriteHandlerPtr dac_10bit_w = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
/*TODO*///		static UINT8 even_byte;
/*TODO*///		struct dac_state *d = &dac[6];
/*TODO*///		int count = (d.bufin - d.bufout) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///		/* warning: this assumes all port writes here are word-sized */
/*TODO*///		/* if the offset is even, just stash the value */
/*TODO*///		if (!(offset & 1))
/*TODO*///		{
/*TODO*///			even_byte = data;
/*TODO*///			return;
/*TODO*///		}
/*TODO*///		data = ((data & 0xff) << 8) | even_byte;
/*TODO*///	
/*TODO*///		/* set the new value */
/*TODO*///		d.value = (INT16)data - 0x200;
/*TODO*///		if (LOG_DAC != 0) logerror("%05X:DAC 10-bit value = %02X\n", cpu_get_pc(), data);
/*TODO*///	
/*TODO*///		/* if we haven't overflowed the buffer, add the value value to it */
/*TODO*///		if (count < DAC_BUFFER_SIZE - 1)
/*TODO*///		{
/*TODO*///			/* if this is the first byte, sync the stream */
/*TODO*///			if (count == 0)
/*TODO*///				stream_update(nondma_stream, 0);
/*TODO*///	
/*TODO*///			/* prescale by the volume */
/*TODO*///			d.buffer[d.bufin] = d.value * (0xff / DAC_VOLUME_SCALE / 2);
/*TODO*///			d.bufin = (d.bufin + 1) & DAC_BUFFER_SIZE_MASK;
/*TODO*///	
/*TODO*///			/* update the clock status */
/*TODO*///			if (++count > d.buftarget)
/*TODO*///				clock_active &= ~0x40;
/*TODO*///		}
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	public static WriteHandlerPtr ataxx_dac_control = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
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
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Peripheral chip dispatcher
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	public static ReadHandlerPtr peripheral_r  = new ReadHandlerPtr() { public int handler(int offset)
/*TODO*///	{
/*TODO*///		int select = offset / 0x80;
/*TODO*///		offset &= 0x7f;
/*TODO*///	
/*TODO*///		switch (select)
/*TODO*///		{
/*TODO*///			case 0:
/*TODO*///				if ((offset & 1) != 0)
/*TODO*///					return 0;
/*TODO*///	
/*TODO*///				/* we have to return 0 periodically so that they handle interrupts */
/*TODO*///				if ((++clock_tick & 7) == 0)
/*TODO*///					return 0;
/*TODO*///	
/*TODO*///				/* if we've filled up all the active channels, we can give this CPU a reset */
/*TODO*///				/* until the next interrupt */
/*TODO*///				{
/*TODO*///					UINT8 result;
/*TODO*///	
/*TODO*///					if (!is_redline)
/*TODO*///						result = ((clock_active >> 1) & 0x3e);
/*TODO*///					else
/*TODO*///						result = ((clock_active << 1) & 0x7e);
/*TODO*///	
/*TODO*///					if (!i186.intr.pending && active_mask && (*active_mask & result) == 0 && ++total_reads > 100)
/*TODO*///					{
/*TODO*///						if (LOG_OPTIMIZATION != 0) logerror("Suspended CPU: active_mask = %02X, result = %02X\n", *active_mask, result);
/*TODO*///						cpu_spinuntil_trigger(CPU_RESUME_TRIGGER);
/*TODO*///					}
/*TODO*///					else if (LOG_OPTIMIZATION != 0)
/*TODO*///					{
/*TODO*///						if (i186.intr.pending) logerror("(can't suspend - interrupt pending)\n");
/*TODO*///						else if (active_mask && (*active_mask & result) != 0) logerror("(can't suspend: mask=%02X result=%02X\n", *active_mask, result);
/*TODO*///					}
/*TODO*///	
/*TODO*///					return result;
/*TODO*///				}
/*TODO*///	
/*TODO*///			case 1:
/*TODO*///				return main_to_sound_comm_r(offset);
/*TODO*///	
/*TODO*///			case 2:
/*TODO*///				return pit8254_r(offset);
/*TODO*///	
/*TODO*///			case 3:
/*TODO*///				if (!has_ym2151)
/*TODO*///					return pit8254_r(offset | 0x80);
/*TODO*///				else
/*TODO*///					return (offset & 1) ? 0 : YM2151_status_port_0_r(offset);
/*TODO*///	
/*TODO*///			case 4:
/*TODO*///				if (is_redline != 0)
/*TODO*///					return pit8254_r(offset | 0x100);
/*TODO*///				else
/*TODO*///					logerror("%05X:Unexpected peripheral read %d/%02X\n", cpu_get_pc(), select, offset);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			default:
/*TODO*///				logerror("%05X:Unexpected peripheral read %d/%02X\n", cpu_get_pc(), select, offset);
/*TODO*///				break;
/*TODO*///		}
/*TODO*///		return 0xff;
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	public static WriteHandlerPtr peripheral_w = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
/*TODO*///		int select = offset / 0x80;
/*TODO*///		offset &= 0x7f;
/*TODO*///	
/*TODO*///		switch (select)
/*TODO*///		{
/*TODO*///			case 1:
/*TODO*///				sound_to_main_comm_w(offset, data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 2:
/*TODO*///				pit8254_w(offset, data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 3:
/*TODO*///				if (!has_ym2151)
/*TODO*///					pit8254_w(offset | 0x80, data);
/*TODO*///				else if (offset == 0)
/*TODO*///					YM2151_register_port_0_w(offset, data);
/*TODO*///				else if (offset == 2)
/*TODO*///					YM2151_data_port_0_w(offset, data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 4:
/*TODO*///				if (is_redline != 0)
/*TODO*///					pit8254_w(offset | 0x100, data);
/*TODO*///				else
/*TODO*///					dac_10bit_w(offset, data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			case 5:	/* Ataxx/WSF/Indy Heat only */
/*TODO*///				ataxx_dac_control(offset, data);
/*TODO*///				break;
/*TODO*///	
/*TODO*///			default:
/*TODO*///				logerror("%05X:Unexpected peripheral write %d/%02X = %02X\n", cpu_get_pc(), select, offset, data);
/*TODO*///				break;
/*TODO*///		}
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
	
	/*************************************
	 *
	 *	Optimizations
	 *
	 *************************************/
	
	public static void leland_i86_optimize_address(int offset)
	{
		if (offset != 0)
			active_mask = new UBytePtr(memory_region(REGION_CPU3) , offset);
		else
			active_mask = null;
	}
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Game-specific handlers
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	public static WriteHandlerPtr ataxx_i86_control_w = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
/*TODO*///		/* compute the bit-shuffled variants of the bits and then write them */
/*TODO*///		int modified = 	((data & 0x01) << 7) |
/*TODO*///						((data & 0x02) << 5) |
/*TODO*///						((data & 0x04) << 3) |
/*TODO*///						((data & 0x08) << 1);
/*TODO*///		leland_i86_control_w(offset, modified);
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
	
	/*************************************
	 *
	 *	Sound CPU memory handlers
	 *
	 *************************************/
	
	public static MemoryReadAddress leland_i86_readmem[] =
	{
		new MemoryReadAddress( 0x00000, 0x03fff, MRA_RAM ),
		new MemoryReadAddress( 0x0c000, 0x0ffff, MRA_BANK6 ),	/* used by Ataxx */
		new MemoryReadAddress( 0x1c000, 0x1ffff, MRA_BANK7 ),	/* used by Super Offroad */
		new MemoryReadAddress( 0x20000, 0xfffff, MRA_ROM ),
		new MemoryReadAddress( -1 )  /* end of table */
	};
	
	public static MemoryWriteAddress leland_i86_writemem[] =
	{
		new MemoryWriteAddress( 0x00000, 0x03fff, MWA_RAM, ram_base ),
		new MemoryWriteAddress( 0x0c000, 0x0ffff, MWA_BANK6 ),
		new MemoryWriteAddress( 0x1c000, 0x1ffff, MWA_BANK7 ),
		new MemoryWriteAddress( 0x20000, 0xfffff, MWA_ROM ),
		new MemoryWriteAddress( -1 )  /* end of table */
	};
	
	public static IOReadPort leland_i86_readport[] =
	{
		new IOReadPort( 0xff00, 0xffff, i186_internal_port_r ),
	    new IOReadPort( -1 )  /* end of table */
	};
	
	public static IOWritePort redline_i86_writeport[] =
	{
		new IOWritePort( 0x6000, 0x6fff, redline_dac_w ),
		new IOWritePort( 0xff00, 0xffff, i186_internal_port_w ),
		new IOWritePort( -1 )  /* end of table */
	};
	
	public static IOWritePort leland_i86_writeport[] =
	{
		new IOWritePort( 0x0000, 0x000b, dac_w ),
		new IOWritePort( 0x0080, 0x008b, dac_w ),
		new IOWritePort( 0x00c0, 0x00cb, dac_w ),
		new IOWritePort( 0xff00, 0xffff, i186_internal_port_w ),
		new IOWritePort( -1 )  /* end of table */
	};
	
/*TODO*///	static IOWritePort ataxx_i86_writeport[] =
/*TODO*///	{
/*TODO*///		new IOWritePort( 0xff00, 0xffff, i186_internal_port_w ),
/*TODO*///		new IOWritePort( -1 )  /* end of table */
/*TODO*///	};
/*TODO*///	
/*TODO*///	
}
