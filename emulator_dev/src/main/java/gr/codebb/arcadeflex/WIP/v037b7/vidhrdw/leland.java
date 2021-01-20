/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.drivers.leland.*;
import static gr.codebb.arcadeflex.WIP.v037b7.sndhrdw.leland.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memcpy;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.common.libc.expressions.sizeof;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ORIENTATION_FLIP_X;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ORIENTATION_FLIP_Y;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.ORIENTATION_SWAP_XY;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_init_used_colors;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_recalc;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_used_colors;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.PALETTE_COLOR_USED;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.cpu_get_pc;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.cpu_getactivecpu;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.cpu_getscanline;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.cpu_getscanlinetime;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.ReadHandlerPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhEofCallbackPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStartPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhStopPtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.VhUpdatePtr;
import gr.codebb.arcadeflex.v037b7.common.fucPtr.WriteHandlerPtr;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.REGION_USER1;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.GfxElement;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.TRANSPARENCY_NONE_RAW;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import gr.codebb.arcadeflex.v056.mame.timer.timer_callback;
import static gr.codebb.arcadeflex.v056.mame.timer.timer_set;
import static gr.codebb.arcadeflex.v056.mame.timerH.TIME_NOW;

public class leland {

    /* constants */
    public static final int VRAM_LO = 0x00000;
    public static final int VRAM_HI = 0x08000;
    public static final int VRAM_SIZE = 0x10000;

    public static final int QRAM_SIZE = 0x10000;

    public static final int VIDEO_WIDTH = 0x28;
    public static final int VIDEO_HEIGHT = 0x1e;

    /* debugging */
    //#define LOG_COMM	0
    public static class vram_state_data {

        char addr;
        char/*UINT8*/ plane;
        char[] /*UINT8*/ latch = new char[2];
    }

    public static class scroll_position {

        char scanline;
        char x, y;
        char/*UINT8*/ gfxbank;
    }
    /* video RAM */
    public static UBytePtr leland_video_ram;
    public static UBytePtr ataxx_qram;
    public static char/*UINT8*/ leland_last_scanline_int;

    /* video RAM bitmap drawing */
    static vram_state_data[] vram_state = new vram_state_data[2];
    static char/*UINT8*/ sync_next_write;

    /* partial screen updating */
    public static UBytePtr video_ram_copy;
    static int next_update_scanline;

    /* scroll background registers */
    static char xscroll;
    static char yscroll;
    static char/*UINT8*/ gfxbank;
    static char/*UINT8*/ scroll_index;
    static scroll_position[] scroll_pos = new scroll_position[VIDEO_HEIGHT];

    static int[] ataxx_pen_usage;

    /**
     * ***********************************
     *
     * Start video hardware
     *
     ************************************
     */
    public static VhStartPtr leland_vh_start = new VhStartPtr() {
        public int handler() {
            for (int i = 0; i < 2; i++) {
                vram_state[i] = new vram_state_data();
            }
            for (int i = 0; i < VIDEO_HEIGHT; i++) {
                scroll_pos[i] = new scroll_position();
            }
            /* allocate memory */
            leland_video_ram = new UBytePtr(VRAM_SIZE);
            video_ram_copy = new UBytePtr(VRAM_SIZE);

            /* error cases */
            if (leland_video_ram == null || video_ram_copy == null) {
                leland_vh_stop.handler();
                return 1;
            }

            /* reset videoram */
            memset(leland_video_ram, 0, VRAM_SIZE);
            memset(video_ram_copy, 0, VRAM_SIZE);

            /* reset scrolling */
            scroll_index = 0;
            //memset(scroll_pos, 0, sizeof(scroll_pos));

            return 0;
        }
    };
    /*TODO*///	
/*TODO*///	
/*TODO*///	public static VhStartPtr ataxx_vh_start = new VhStartPtr() { public int handler() 
/*TODO*///	{
/*TODO*///		
/*TODO*///		const struct GfxElement *gfx = Machine.gfx[0];
/*TODO*///		UINT32 usage[2];
/*TODO*///		int i, x, y;
/*TODO*///	
/*TODO*///		/* first do the standard stuff */
/*TODO*///		if (leland_vh_start())
/*TODO*///			return 1;
/*TODO*///	
/*TODO*///		/* allocate memory */
/*TODO*///		ataxx_qram = malloc(QRAM_SIZE);
/*TODO*///		ataxx_pen_usage = malloc(gfx.total_elements * 2 * sizeof(UINT32));
/*TODO*///	
/*TODO*///		/* error cases */
/*TODO*///	    if (!ataxx_qram || !ataxx_pen_usage)
/*TODO*///	    {
/*TODO*///	    	ataxx_vh_stop();
/*TODO*///			return 1;
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* build up color usage */
/*TODO*///		for (i = 0; i < gfx.total_elements; i++)
/*TODO*///		{
/*TODO*///			UINT8 *src = gfx.gfxdata + i * gfx.char_modulo;
/*TODO*///	
/*TODO*///			usage[0] = usage[1] = 0;
/*TODO*///			for (y = 0; y < gfx.height; y++)
/*TODO*///			{
/*TODO*///				for (x = 0; x < gfx.width; x++)
/*TODO*///				{
/*TODO*///					int color = src[x];
/*TODO*///					usage[color >> 5] |= 1 << (color & 31);
/*TODO*///				}
/*TODO*///				src += gfx.line_modulo;
/*TODO*///			}
/*TODO*///			ataxx_pen_usage[i * 2 + 0] = usage[0];
/*TODO*///			ataxx_pen_usage[i * 2 + 1] = usage[1];
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* reset QRAM */
/*TODO*///		memset(ataxx_qram, 0, QRAM_SIZE);
/*TODO*///		return 0;
/*TODO*///	} };
/*TODO*///	

    /**
     * ***********************************
     *
     * Stop video hardware
     *
     ************************************
     */
    public static VhStopPtr leland_vh_stop = new VhStopPtr() {
        public void handler() {
            if (leland_video_ram != null) {
                leland_video_ram = null;
            }

            if (video_ram_copy != null) {
                video_ram_copy = null;
            }
        }
    };

    /*TODO*///	
/*TODO*///	public static VhStopPtr ataxx_vh_stop = new VhStopPtr() { public void handler() 
/*TODO*///	{
/*TODO*///		leland_vh_stop();
/*TODO*///	
/*TODO*///		if (ataxx_qram != 0)
/*TODO*///			free(ataxx_qram);
/*TODO*///		ataxx_qram = NULL;
/*TODO*///	
/*TODO*///		if (ataxx_pen_usage != 0)
/*TODO*///			free(ataxx_pen_usage);
/*TODO*///		ataxx_pen_usage = NULL;
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	
    /**
     * ***********************************
     *
     * Scrolling and banking
     *
     ************************************
     */
    public static WriteHandlerPtr leland_gfx_port_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int scanline = leland_last_scanline_int;

            scroll_position[] scroll;
            int scroll_offs = 0;

            /* treat anything during the VBLANK as scanline 0 */
            if (scanline > Machine.visible_area.max_y) {
                scanline = 0;
            }

            switch (offset) {
                case -1:
                    gfxbank = (char) (data & 0xFF);
                    break;
                case 0:
                    xscroll = (char) ((xscroll & 0xff00) | (data & 0x00ff));
                    break;
                case 1:
                    xscroll = (char) ((xscroll & 0x00ff) | ((data << 8) & 0xff00));
                    break;
                case 2:
                    yscroll = (char) ((yscroll & 0xff00) | (data & 0x00ff));
                    break;
                case 3:
                    yscroll = (char) ((yscroll & 0x00ff) | ((data << 8) & 0xff00));
                    break;
            }

            /* update if necessary */
            scroll = scroll_pos;
            scroll_offs = scroll_index;
            if (xscroll != scroll[scroll_offs].x || yscroll != scroll[scroll_offs].y || gfxbank != scroll[scroll_offs].gfxbank) {
                /* determine which entry to use */
                if (scroll[scroll_offs].scanline != scanline && scroll_index < VIDEO_HEIGHT - 1) {
                    scroll_offs++;
                    scroll_index++;
                }

                /* fill in the data */
                scroll[scroll_offs].scanline = (char) scanline;
                scroll[scroll_offs].x = xscroll;
                scroll[scroll_offs].y = yscroll;
                scroll[scroll_offs].gfxbank = gfxbank;
            }
        }
    };

    /**
     * ***********************************
     *
     * Video address setting
     *
     ************************************
     */
    static void leland_video_addr_w(int offset, int data, int num) {
        vram_state_data state = vram_state[num];

        if (offset == 0) {
            state.addr = (char) ((state.addr & 0x7f00) | (data & 0x00ff));
            state.plane = 0;
        } else {
            state.addr = (char) (((data << 8) & 0x7f00) | (state.addr & 0x00ff));
            state.plane = 0;
        }

        if (num == 0) {
            sync_next_write = (state.addr >= 0x7800) ? (char) 1 : (char) 0;
        }
    }

    /**
     * ***********************************
     *
     * Flush data from VRAM into our copy
     *
     ************************************
     */
    static void update_for_scanline(int scanline) {
        int i;

        /* skip if we're behind the times */
        if (scanline <= next_update_scanline) {
            return;
        }

        /* update all scanlines */
        for (i = next_update_scanline; i < scanline; i++) {
            memcpy(video_ram_copy, i * 128 + VRAM_LO, leland_video_ram, i * 128 + VRAM_LO, 0x51);
            memcpy(video_ram_copy, i * 128 + VRAM_HI, leland_video_ram, i * 128 + VRAM_HI, 0x51);
        }

        /* set the new last update */
        next_update_scanline = scanline;
    }

    /**
     * ***********************************
     *
     * Common video RAM read
     *
     ************************************
     */
    static int leland_vram_port_r(int offset, int num) {
        vram_state_data state = vram_state[num];
        int addr = state.addr;
        int plane = state.plane;
        int inc = (offset >> 3) & 1;
        int ret;

        switch (offset & 7) {
            case 3:
                /* read hi/lo (alternating) */
                ret = leland_video_ram.read(addr + plane * VRAM_HI);
                addr += inc & plane;
                plane ^= 1;
                break;

            case 5:
                /* read hi */
                ret = leland_video_ram.read(addr + VRAM_HI);
                addr += inc;
                break;

            case 6:
                /* read lo */
                ret = leland_video_ram.read(addr + VRAM_LO);
                addr += inc;
                break;

            default:
                logerror("CPU #%d %04x Warning: Unknown video port %02x read (address=%04x)\n",
                        cpu_getactivecpu(), cpu_get_pc(), offset, addr);
                ret = 0;
                break;
        }

        state.addr = (char) (addr & 0x7fff);
        state.plane = (char) (plane & 0xFF);

        //if (LOG_COMM && addr >= 0x7800)
        //logerror("%04X:%s comm read %04X = %02X\n", cpu_getpreviouspc(), num ? "slave" : "master", addr, ret);
        return ret;
    }

    /**
     * ***********************************
     *
     * Common video RAM write
     *
     ************************************
     */
    static void leland_vram_port_w(int offset, int data, int num) {
        vram_state_data state = vram_state[num];
        int addr = state.addr;
        int plane = state.plane;
        int inc = (offset >> 3) & 1;
        int trans = (offset >> 4) & num;

        /* if we're writing "behind the beam", make sure we've cached what was there */
        if (addr < 0x7800) {
            int cur_scanline = cpu_getscanline();
            int mod_scanline = addr / 0x80;

            if (cur_scanline != next_update_scanline && mod_scanline < cur_scanline) {
                update_for_scanline(cur_scanline);
            }
        }

        //if (LOG_COMM && addr >= 0x7800)
        //logerror("%04X:%s comm write %04X = %02X\n", cpu_getpreviouspc(), num ? "slave" : "master", addr, data);
        /* based on the low 3 bits of the offset, update the destination */
        switch (offset & 7) {
            case 1:
                /* write hi = data, lo = latch */
                leland_video_ram.write(addr + VRAM_HI, data);
                leland_video_ram.write(addr + VRAM_LO, state.latch[0]);
                addr += inc;
                break;
            case 2:
                /* write hi = latch, lo = data */
                leland_video_ram.write(addr + VRAM_HI, state.latch[1]);
                leland_video_ram.write(addr + VRAM_LO, data);
                addr += inc;
                break;
            case 3:
                /* write hi/lo = data (alternating) */
                if (trans != 0) {
                    if ((data & 0xf0) == 0) {
                        data |= leland_video_ram.read(addr + plane * VRAM_HI) & 0xf0;
                    }
                    if ((data & 0x0f) == 0) {
                        data |= leland_video_ram.read(addr + plane * VRAM_HI) & 0x0f;
                    }
                }
                leland_video_ram.write(addr + plane * VRAM_HI, data);
                addr += inc & plane;
                plane ^= 1;
                break;
            case 5:
                /* write hi = data */
                state.latch[1] = (char) (data & 0xFF);
                if (trans != 0) {
                    if ((data & 0xf0) == 0) {
                        data |= leland_video_ram.read(addr + VRAM_HI) & 0xf0;
                    }
                    if ((data & 0x0f) == 0) {
                        data |= leland_video_ram.read(addr + VRAM_HI) & 0x0f;
                    }
                }
                leland_video_ram.write(addr + VRAM_HI, data);
                addr += inc;
                break;
            case 6:
                /* write lo = data */
                state.latch[0] = (char) (data & 0xFF);
                if (trans != 0) {
                    if ((data & 0xf0) == 0) {
                        data |= leland_video_ram.read(addr + VRAM_LO) & 0xf0;
                    }
                    if ((data & 0x0f) == 0) {
                        data |= leland_video_ram.read(addr + VRAM_LO) & 0x0f;
                    }
                }
                leland_video_ram.write(addr + VRAM_LO, data);
                addr += inc;
                break;

            default:
                logerror("CPU #%d %04x Warning: Unknown video port %02x write (address=%04x value=%02x)\n",
                        cpu_getactivecpu(), cpu_get_pc(), offset, addr);
                break;
        }

        /* update the address and plane */
        state.addr = (char) (addr & 0x7fff);
        state.plane = (char) (plane & 0xFF);
    }

    /**
     * ***********************************
     *
     * Master video RAM read/write
     *
     ************************************
     */
    public static WriteHandlerPtr leland_master_video_addr_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            leland_video_addr_w(offset, data, 0);
        }
    };

    public static timer_callback leland_delayed_mvram_w = new timer_callback() {
        public void handler(int param) {
            int num = (param >> 16) & 1;
            int offset = (param >> 8) & 0xff;
            int data = param & 0xff;
            leland_vram_port_w(offset, data, num);
        }
    };

    public static WriteHandlerPtr leland_mvram_port_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (sync_next_write != 0) {
                timer_set(TIME_NOW, 0x00000 | (offset << 8) | data, leland_delayed_mvram_w);
                sync_next_write = 0;
            } else {
                leland_vram_port_w(offset, data, 0);
            }
        }
    };

    public static ReadHandlerPtr leland_mvram_port_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return leland_vram_port_r(offset, 0);
        }
    };

    /**
     * ***********************************
     *
     * Slave video RAM read/write
     *
     ************************************
     */
    public static WriteHandlerPtr leland_slave_video_addr_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            leland_video_addr_w(offset, data, 1);
        }
    };

    public static WriteHandlerPtr leland_svram_port_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            leland_vram_port_w(offset, data, 1);
        }
    };

    public static ReadHandlerPtr leland_svram_port_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return leland_vram_port_r(offset, 1);
        }
    };

    /*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Ataxx master video RAM read/write
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	public static WriteHandlerPtr ataxx_mvram_port_w = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
/*TODO*///		offset = ((offset >> 1) & 0x07) | ((offset << 3) & 0x08) | (offset & 0x10);
/*TODO*///		if (sync_next_write != 0)
/*TODO*///		{
/*TODO*///			timer_set(TIME_NOW, 0x00000 | (offset << 8) | data, leland_delayed_mvram_w);
/*TODO*///			sync_next_write = 0;
/*TODO*///		}
/*TODO*///		else
/*TODO*///			leland_vram_port_w(offset, data, 0);
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	public static WriteHandlerPtr ataxx_svram_port_w = new WriteHandlerPtr() {public void handler(int offset, int data)
/*TODO*///	{
/*TODO*///		offset = ((offset >> 1) & 0x07) | ((offset << 3) & 0x08) | (offset & 0x10);
/*TODO*///		leland_vram_port_w(offset, data, 1);
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Ataxx slave video RAM read/write
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	public static ReadHandlerPtr ataxx_mvram_port_r  = new ReadHandlerPtr() { public int handler(int offset)
/*TODO*///	{
/*TODO*///		offset = ((offset >> 1) & 0x07) | ((offset << 3) & 0x08) | (offset & 0x10);
/*TODO*///	    return leland_vram_port_r(offset, 0);
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	public static ReadHandlerPtr ataxx_svram_port_r  = new ReadHandlerPtr() { public int handler(int offset)
/*TODO*///	{
/*TODO*///		offset = ((offset >> 1) & 0x07) | ((offset << 3) & 0x08) | (offset & 0x10);
/*TODO*///	    return leland_vram_port_r(offset, 1);
/*TODO*///	} };
    /**
     * ***********************************
     *
     * End-of-frame routine
     *
     ************************************
     */
    /**
     * ***********************************
     * End-of-frame routine ***********************************
     */
    public static timer_callback scanline_reset = new timer_callback() {
        public void handler(int param) {
            /* flush the remaining scanlines */
            update_for_scanline(256);
            next_update_scanline = 0;

            /* update the DACs if they're on */
            if ((u8_leland_dac_control & 0x01) == 0) {
                leland_dac_update(0, new UBytePtr(video_ram_copy, VRAM_LO + 0x50));
            }
            if ((u8_leland_dac_control & 0x02) == 0) {
                leland_dac_update(1, new UBytePtr(video_ram_copy, VRAM_HI + 0x50));
            }
            u8_leland_dac_control = 3;
        }
    };

    public static VhEofCallbackPtr leland_vh_eof = new VhEofCallbackPtr() {
        public void handler() {
            /* reset scrolling */
            scroll_index = 0;
            scroll_pos[0].scanline = 0;
            scroll_pos[0].x = xscroll;
            scroll_pos[0].y = yscroll;
            scroll_pos[0].gfxbank = gfxbank;

            /* update anything remaining */
            update_for_scanline(VIDEO_HEIGHT * 8);

            /* set a timer to go off at the top of the frame */
            timer_set(cpu_getscanlinetime(0), 0, scanline_reset);
        }
    };

    /**
     * ***********************************
     *
     * ROM-based refresh routine
     *
     ************************************
     */
    public static VhUpdatePtr leland_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            UBytePtr background_prom = memory_region(REGION_USER1);
            GfxElement gfx = Machine.gfx[0];
            int total_elements = gfx.total_elements;
            int[] /*UINT8*/ background_usage = new int[8];
            int x, y, chunk;

            /* update anything remaining */
            update_for_scanline(VIDEO_HEIGHT * 8);

            /* loop over scrolling chunks */
 /* it's okay to do this before the palette calc because */
 /* these values are raw indexes, not pens */
            memset(background_usage, 0, sizeof(background_usage));
            for (chunk = 0; chunk <= scroll_index; chunk++) {
                int char_bank = ((scroll_pos[chunk].gfxbank >> 4) & 0x03) * 0x0400;
                int prom_bank = ((scroll_pos[chunk].gfxbank >> 3) & 0x01) * 0x2000;

                /* determine scrolling parameters */
                int xfine = scroll_pos[chunk].x % 8;
                int yfine = scroll_pos[chunk].y % 8;
                int xcoarse = scroll_pos[chunk].x / 8;
                int ycoarse = scroll_pos[chunk].y / 8;
                rectangle clip;

                /* make a clipper */
                clip = new rectangle(Machine.visible_area);
                if (chunk != 0) {
                    clip.min_y = scroll_pos[chunk].scanline;
                }
                if (chunk != scroll_index) {
                    clip.max_y = scroll_pos[chunk + 1].scanline - 1;
                }

                /* draw what's visible to the main bitmap */
                for (y = clip.min_y / 8; y < clip.max_y / 8 + 2; y++) {
                    int ysum = ycoarse + y;
                    for (x = 0; x < VIDEO_WIDTH + 1; x++) {
                        int xsum = xcoarse + x;
                        int offs = ((xsum << 0) & 0x000ff)
                                | ((ysum << 8) & 0x01f00)
                                | prom_bank
                                | ((ysum << 9) & 0x1c000);
                        int code = background_prom.read(offs)
                                | ((ysum << 2) & 0x300)
                                | char_bank;
                        int color = (code >> 5) & 7;

                        /* draw to the bitmap */
                        drawgfx(bitmap, gfx,
                                code, 8 * color, 0, 0,
                                8 * x - xfine, 8 * y - yfine,
                                clip, TRANSPARENCY_NONE_RAW, 0);

                        /* update color usage */
                        background_usage[color] |= gfx.pen_usage[code & (total_elements - 1)];
                    }
                }
            }

            /* build the palette */
            palette_init_used_colors();
            for (y = 0; y < 8; y++) {
                int/*UINT8*/ usage = background_usage[y];
                for (x = 0; x < 8; x++) {
                    if ((usage & (1 << x)) != 0) {
                        int p;
                        for (p = 0; p < 16; p++) {
                            palette_used_colors.write(p * 64 + y * 8 + x, PALETTE_COLOR_USED);
                        }
                    }
                }
            }
            palette_recalc();

            /* Merge the two bitmaps together */
            if (bitmap.depth == 8) {
                draw_bitmap_8(bitmap);
            } else {
                throw new UnsupportedOperationException("Unsupported");
                /*TODO*///			draw_bitmap_16(bitmap);
            }
        }
    };

    /*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	RAM-based refresh routine
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	public static VhUpdatePtr ataxx_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
/*TODO*///	{
/*TODO*///		const struct GfxElement *gfx = Machine.gfx[0];
/*TODO*///		int total_elements = gfx.total_elements;
/*TODO*///		UINT32 background_usage[2];
/*TODO*///		int x, y, chunk;
/*TODO*///	
/*TODO*///		/* update anything remaining */
/*TODO*///		update_for_scanline(VIDEO_HEIGHT * 8);
/*TODO*///	
/*TODO*///		/* loop over scrolling chunks */
/*TODO*///		/* it's okay to do this before the palette calc because */
/*TODO*///		/* these values are raw indexes, not pens */
/*TODO*///		memset(background_usage, 0, sizeof(background_usage));
/*TODO*///		for (chunk = 0; chunk <= scroll_index; chunk++)
/*TODO*///		{
/*TODO*///			/* determine scrolling parameters */
/*TODO*///			int xfine = scroll_pos[chunk].x % 8;
/*TODO*///			int yfine = scroll_pos[chunk].y % 8;
/*TODO*///			int xcoarse = scroll_pos[chunk].x / 8;
/*TODO*///			int ycoarse = scroll_pos[chunk].y / 8;
/*TODO*///			struct rectangle clip;
/*TODO*///	
/*TODO*///			/* make a clipper */
/*TODO*///			clip = Machine.visible_area;
/*TODO*///			if (chunk != 0)
/*TODO*///				clip.min_y = scroll_pos[chunk].scanline;
/*TODO*///			if (chunk != scroll_index)
/*TODO*///				clip.max_y = scroll_pos[chunk + 1].scanline - 1;
/*TODO*///	
/*TODO*///			/* draw what's visible to the main bitmap */
/*TODO*///			for (y = clip.min_y / 8; y < clip.max_y / 8 + 2; y++)
/*TODO*///			{
/*TODO*///				int ysum = ycoarse + y;
/*TODO*///				for (x = 0; x < VIDEO_WIDTH + 1; x++)
/*TODO*///				{
/*TODO*///					int xsum = xcoarse + x;
/*TODO*///					int offs = ((ysum & 0x40) << 9) + ((ysum & 0x3f) << 8) + (xsum & 0xff);
/*TODO*///					int code = ataxx_qram[offs] | ((ataxx_qram[offs + 0x4000] & 0x7f) << 8);
/*TODO*///	
/*TODO*///					/* draw to the bitmap */
/*TODO*///					drawgfx(bitmap, gfx,
/*TODO*///							code, 0, 0, 0,
/*TODO*///							8 * x - xfine, 8 * y - yfine,
/*TODO*///							&clip, TRANSPARENCY_NONE_RAW, 0);
/*TODO*///	
/*TODO*///					/* update color usage */
/*TODO*///					background_usage[0] |= ataxx_pen_usage[(code & (total_elements - 1)) * 2 + 0];
/*TODO*///					background_usage[1] |= ataxx_pen_usage[(code & (total_elements - 1)) * 2 + 1];
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* build the palette */
/*TODO*///		palette_init_used_colors();
/*TODO*///		for (y = 0; y < 2; y++)
/*TODO*///		{
/*TODO*///			UINT32 usage = background_usage[y];
/*TODO*///			for (x = 0; x < 32; x++)
/*TODO*///				if (usage & (1 << x))
/*TODO*///				{
/*TODO*///					int p;
/*TODO*///					for (p = 0; p < 16; p++)
/*TODO*///						palette_used_colors[p * 64 + y * 32 + x] = PALETTE_COLOR_USED;
/*TODO*///				}
/*TODO*///		}
/*TODO*///		palette_recalc();
/*TODO*///	
/*TODO*///		/* Merge the two bitmaps together */
/*TODO*///		if (bitmap.depth == 8)
/*TODO*///			draw_bitmap_8(bitmap);
/*TODO*///		else
/*TODO*///			draw_bitmap_16(bitmap);
/*TODO*///	} };
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Depth-specific refresh
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
/*TODO*///	#define ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, x, y, xadv)	\
/*TODO*///		if (orientation != 0)													\
/*TODO*///		{																	\
/*TODO*///			int dy = bitmap.line[1] - bitmap.line[0];						\
/*TODO*///			int tx = x, ty = y, temp;										\
/*TODO*///			if ((orientation & ORIENTATION_SWAP_XY) != 0)							\
/*TODO*///			{																\
/*TODO*///				temp = tx; tx = ty; ty = temp;								\
/*TODO*///				xadv = dy / (bitmap.depth / 8);							\
/*TODO*///			}																\
/*TODO*///			if ((orientation & ORIENTATION_FLIP_X) != 0)							\
/*TODO*///			{																\
/*TODO*///				tx = bitmap.width - 1 - tx;								\
/*TODO*///				if (!(orientation & ORIENTATION_SWAP_XY)) xadv = -xadv;		\
/*TODO*///			}																\
/*TODO*///			if ((orientation & ORIENTATION_FLIP_Y) != 0)							\
/*TODO*///			{																\
/*TODO*///				ty = bitmap.height - 1 - ty;								\
/*TODO*///				if ((orientation & ORIENTATION_SWAP_XY)) xadv = -xadv;		\
/*TODO*///			}																\
/*TODO*///			/* can't lookup line because it may be negative! */				\
/*TODO*///			dst = (TYPE *)(bitmap.line[0] + dy * ty) + tx;					\
/*TODO*///		}
/*TODO*///	
/*TODO*///	#define INCLUDE_DRAW_CORE
/*TODO*///	
/*TODO*///	#define DRAW_FUNC draw_bitmap_8
/*TODO*///	#define TYPE UINT8
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	#define DRAW_FUNC draw_bitmap_16
/*TODO*///	#define TYPE UINT16
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	
/*TODO*///	#else
/*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *	Bitmap blending routine
/*TODO*///	 *
/*TODO*///	 *************************************/
/*TODO*///	
    public static void draw_bitmap_8(osd_bitmap bitmap) {
        char[] pens = Machine.pens;//[0];
        int orientation = Machine.orientation;
        int x, y;

        /* draw any non-transparent scanlines from the VRAM directly */
        for (y = Machine.visible_area.min_y; y <= Machine.visible_area.max_y; y++) {
            UBytePtr srclo = new UBytePtr(video_ram_copy, y * 128 + VRAM_LO);
            UBytePtr srchi = new UBytePtr(video_ram_copy, y * 128 + VRAM_HI);
            UBytePtr dst = new UBytePtr(bitmap.line[y]);
            int xadv = 1;

            /* adjust in case we're oddly oriented */
            if (orientation != 0) {
                int dy = bitmap.line[1].offset - bitmap.line[0].offset;
                int tx = 0, ty = y, temp;
                if ((orientation & ORIENTATION_SWAP_XY) != 0) {
                    temp = tx;
                    tx = ty;
                    ty = temp;
                    xadv = dy / (bitmap.depth / 8);
                }
                if ((orientation & ORIENTATION_FLIP_X) != 0) {
                    tx = bitmap.width - 1 - tx;
                    if ((orientation & ORIENTATION_SWAP_XY) == 0) {
                        xadv = -xadv;
                    }
                }
                if ((orientation & ORIENTATION_FLIP_Y) != 0) {
                    ty = bitmap.height - 1 - ty;
                    if ((orientation & ORIENTATION_SWAP_XY) != 0) {
                        xadv = -xadv;
                    }
                }
                /* can't lookup line because it may be negative! */
                dst = new UBytePtr(bitmap.line[0], (dy * ty) + tx);
            }

            /* redraw the scanline */
            for (x = 0; x < VIDEO_WIDTH * 2; x++) {
                char data = (char) ((srclo.readinc() << 8) | srchi.readinc());

                dst.write(pens[dst.read() | ((data & 0xf000) >> 6)]);
                dst.offset += xadv;
                dst.write(pens[dst.read() | ((data & 0x0f00) >> 2)]);
                dst.offset += xadv;
                dst.write(pens[dst.read() | ((data & 0x00f0) << 2)]);
                dst.offset += xadv;
                dst.write(pens[dst.read() | ((data & 0x000f) << 6)]);
                dst.offset += xadv;
            }
        }
    }

    /*TODO*///	void DRAW_FUNC(struct osd_bitmap *bitmap)
/*TODO*///	{
/*TODO*///		const UINT16 *pens = &Machine.pens[0];
/*TODO*///		int orientation = Machine.orientation;
/*TODO*///		int x, y;
/*TODO*///	
/*TODO*///		/* draw any non-transparent scanlines from the VRAM directly */
/*TODO*///		for (y = Machine.visible_area.min_y; y <= Machine.visible_area.max_y; y++)
/*TODO*///		{
/*TODO*///			UINT8 *srclo = &video_ram_copy[y * 128 + VRAM_LO];
/*TODO*///			UINT8 *srchi = &video_ram_copy[y * 128 + VRAM_HI];
/*TODO*///			TYPE *dst = (TYPE *)bitmap.line[y];
/*TODO*///			int xadv = 1;
/*TODO*///	
/*TODO*///			/* adjust in case we're oddly oriented */
/*TODO*///			ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, 0, y, xadv);
/*TODO*///	
/*TODO*///			/* redraw the scanline */
/*TODO*///			for (x = 0; x < VIDEO_WIDTH*2; x++)
/*TODO*///			{
/*TODO*///				UINT16 data = (*srclo++ << 8) | *srchi++;
/*TODO*///	
/*TODO*///				*dst = pens[*dst | ((data & 0xf000) >> 6)];
/*TODO*///				dst += xadv;
/*TODO*///				*dst = pens[*dst | ((data & 0x0f00) >> 2)];
/*TODO*///				dst += xadv;
/*TODO*///				*dst = pens[*dst | ((data & 0x00f0) << 2)];
/*TODO*///				dst += xadv;
/*TODO*///				*dst = pens[*dst | ((data & 0x000f) << 6)];
/*TODO*///				dst += xadv;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	#endif
}
