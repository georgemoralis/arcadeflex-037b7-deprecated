/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.machine.twincobr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.crtc6845.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;

public class twincobr {

    public static UBytePtr twincobr_bgvideoram;
    public static UBytePtr twincobr_fgvideoram;

    public static int wardner_sprite_hack = 0;
    /* Required for weird sprite priority in wardner  */
 /* when hero is in shop. Hero should cover shop owner */
 /*TODO*///	#define READ_WORD_Z80(x) (*(UBytePtr )(x) + (*(UBytePtr )(x+1) << 8))
/*TODO*///	#define WRITE_WORD_Z80(a, d) (*(UBytePtr )(a) = d & 0xff, (*(UBytePtr )(a+1) = (d>>8) & 0xff))
/*TODO*///	
    public static int[] twincobr_bgvideoram_size = new int[1];
    public static int[] twincobr_fgvideoram_size = new int[1];
    static int txscrollx = 0;
    static int txscrolly = 0;
    static int fgscrollx = 0;
    static int fgscrolly = 0;
    static int bgscrollx = 0;
    static int bgscrolly = 0;
    public static int twincobr_fg_rom_bank = 0;
    public static int twincobr_bg_ram_bank = 0;
    public static int twincobr_display_on = 1;
    public static int twincobr_flip_screen = 0;
    public static int twincobr_flip_x_base = 0x37;
    /* value to 0 the X scroll offsets (non-flip) */
    public static int twincobr_flip_y_base = 0x1e;
    /* value to 0 the Y scroll offsets (non-flip) */

    static int txoffs = 0;
    static int bgoffs = 0;
    static int fgoffs = 0;
    static int scroll_x = 0;
    static int scroll_y = 0;

    static int vidbaseaddr = 0;
    static int scroll_realign_x = 0;

    /**
     * *********************** Wardner variables ******************************
     */
    static int tx_offset_lsb = 0;
    static int tx_offset_msb = 0;
    static int bg_offset_lsb = 0;
    static int bg_offset_msb = 0;
    static int fg_offset_lsb = 0;
    static int fg_offset_msb = 0;
    static int tx_scrollx_lsb = 0;
    static int tx_scrollx_msb = 0;
    static int tx_scrolly_lsb = 0;
    static int tx_scrolly_msb = 0;
    static int bg_scrollx_lsb = 0;
    static int bg_scrollx_msb = 0;
    static int bg_scrolly_lsb = 0;
    static int bg_scrolly_msb = 0;
    static int fg_scrollx_lsb = 0;
    static int fg_scrollx_msb = 0;
    static int fg_scrolly_lsb = 0;
    static int fg_scrolly_msb = 0;

    public static VhStartPtr twincobr_vh_start = new VhStartPtr() {
        public int handler() {
            /* the video RAM is accessed via ports, it's not memory mapped */
            videoram_size[0] = 0x1000;
            twincobr_bgvideoram_size[0] = 0x4000;
            /* banked two times 0x2000 */
            twincobr_fgvideoram_size[0] = 0x2000;

            videoram = new UBytePtr(videoram_size[0]);
            memset(videoram, 0, videoram_size[0]);

            twincobr_fgvideoram = new UBytePtr(twincobr_fgvideoram_size[0]);
            memset(twincobr_fgvideoram, 0, twincobr_fgvideoram_size[0]);

            twincobr_bgvideoram = new UBytePtr(twincobr_bgvideoram_size[0]);
            memset(twincobr_bgvideoram, 0, twincobr_bgvideoram_size[0]);

            if ((dirtybuffer = new char[twincobr_bgvideoram_size[0]]) == null) {
                twincobr_bgvideoram = null;
                twincobr_fgvideoram = null;
                videoram = null;
                return 1;
            }
            memset(dirtybuffer, 1, twincobr_bgvideoram_size[0]);

            if ((tmpbitmap = bitmap_alloc(Machine.drv.screen_width, 2 * Machine.drv.screen_height)) == null) {
                dirtybuffer = null;
                twincobr_bgvideoram = null;
                twincobr_fgvideoram = null;
                videoram = null;
                return 1;
            }

            return 0;
        }
    };

    public static VhStopPtr twincobr_vh_stop = new VhStopPtr() {
        public void handler() {
            bitmap_free(tmpbitmap);
            dirtybuffer = null;
            twincobr_bgvideoram = null;
            twincobr_fgvideoram = null;
            videoram = null;
        }
    };

    public static ReadHandlerPtr twincobr_crtc_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return crtc6845_register_r.handler(offset);
        }
    };

    public static WriteHandlerPtr twincobr_crtc_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset == 0) {
                crtc6845_address_w.handler(offset, data);
            }
            if (offset == 2) {
                crtc6845_register_w.handler(offset, data);
            }
        }
    };
    public static ReadHandlerPtr twincobr_txoffs_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return txoffs / 2;
        }
    };
    public static WriteHandlerPtr twincobr_txoffs_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            txoffs = (2 * data) % videoram_size[0];
        }
    };
    public static ReadHandlerPtr twincobr_txram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return videoram.READ_WORD(txoffs);
        }
    };
    public static WriteHandlerPtr twincobr_txram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            videoram.WRITE_WORD(txoffs, data);
        }
    };

    public static WriteHandlerPtr twincobr_bgoffs_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            bgoffs = (2 * data) % (twincobr_bgvideoram_size[0] >> 1);
        }
    };
    public static ReadHandlerPtr twincobr_bgram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return twincobr_bgvideoram.READ_WORD(bgoffs + twincobr_bg_ram_bank);
        }
    };
    public static WriteHandlerPtr twincobr_bgram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            twincobr_bgvideoram.WRITE_WORD(bgoffs + twincobr_bg_ram_bank, data);
            dirtybuffer[bgoffs / 2] = 1;
        }
    };

    public static WriteHandlerPtr twincobr_fgoffs_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            fgoffs = (2 * data) % twincobr_fgvideoram_size[0];
        }
    };
    public static ReadHandlerPtr twincobr_fgram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return twincobr_fgvideoram.READ_WORD(fgoffs);
        }
    };
    public static WriteHandlerPtr twincobr_fgram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            twincobr_fgvideoram.WRITE_WORD(fgoffs, data);
        }
    };

    public static WriteHandlerPtr twincobr_txscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset == 0) {
                txscrollx = data;
            } else {
                txscrolly = data;
            }
        }
    };

    public static WriteHandlerPtr twincobr_bgscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset == 0) {
                bgscrollx = data;
            } else {
                bgscrolly = data;
            }
        }
    };

    public static WriteHandlerPtr twincobr_fgscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset == 0) {
                fgscrollx = data;
            } else {
                fgscrolly = data;
            }
        }
    };

    public static WriteHandlerPtr twincobr_exscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) /* Extra unused video layer */ {
            if (offset == 0) {
                logerror("PC - write %04x to extra video layer Y scroll register\n", data);
            } else {
                logerror("PC - write %04x to extra video layer scroll X register\n", data);
            }
        }
    };

    /**
     * ****************** Wardner interface to this hardware
     * *******************
     */
    public static WriteHandlerPtr wardner_txlayer_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset == 0) {
                tx_offset_lsb = data;
            }
            if (offset == 1) {
                tx_offset_msb = (data << 8);
            }
            twincobr_txoffs_w.handler(0, tx_offset_msb | tx_offset_lsb);
        }
    };
    public static WriteHandlerPtr wardner_bglayer_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset == 0) {
                bg_offset_lsb = data;
            }
            if (offset == 1) {
                bg_offset_msb = (data << 8);
            }
            twincobr_bgoffs_w.handler(0, bg_offset_msb | bg_offset_lsb);
        }
    };
    public static WriteHandlerPtr wardner_fglayer_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (offset == 0) {
                fg_offset_lsb = data;
            }
            if (offset == 1) {
                fg_offset_msb = (data << 8);
            }
            twincobr_fgoffs_w.handler(0, fg_offset_msb | fg_offset_lsb);
        }
    };

    public static WriteHandlerPtr wardner_txscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((offset & 2) != 0) {
                if (offset == 2) {
                    tx_scrollx_lsb = data;
                }
                if (offset == 3) {
                    tx_scrollx_msb = (data << 8);
                }
                twincobr_txscroll_w.handler(2, tx_scrollx_msb | tx_scrollx_lsb);
            } else {
                if (offset == 0) {
                    tx_scrolly_lsb = data;
                }
                if (offset == 1) {
                    tx_scrolly_msb = (data << 8);
                }
                twincobr_txscroll_w.handler(0, tx_scrolly_msb | tx_scrolly_lsb);
            }
        }
    };
    public static WriteHandlerPtr wardner_bgscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((offset & 2) != 0) {
                if (offset == 2) {
                    bg_scrollx_lsb = data;
                }
                if (offset == 3) {
                    bg_scrollx_msb = (data << 8);
                }
                twincobr_bgscroll_w.handler(2, bg_scrollx_msb | bg_scrollx_lsb);
            } else {
                if (offset == 0) {
                    bg_scrolly_lsb = data;
                }
                if (offset == 1) {
                    bg_scrolly_msb = (data << 8);
                }
                twincobr_bgscroll_w.handler(0, bg_scrolly_msb | bg_scrolly_lsb);
            }
        }
    };
    public static WriteHandlerPtr wardner_fgscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if ((offset & 2) != 0) {
                if (offset == 2) {
                    fg_scrollx_lsb = data;
                }
                if (offset == 3) {
                    fg_scrollx_msb = (data << 8);
                }
                twincobr_fgscroll_w.handler(2, fg_scrollx_msb | fg_scrollx_lsb);
            } else {
                if (offset == 0) {
                    fg_scrolly_lsb = data;
                }
                if (offset == 1) {
                    fg_scrolly_msb = (data << 8);
                }
                twincobr_fgscroll_w.handler(0, fg_scrolly_msb | fg_scrolly_lsb);
            }
        }
    };

    public static ReadHandlerPtr wardner_videoram_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            int memdata = 0;
            switch (offset) {
                case 0:
                    memdata = twincobr_txram_r.handler(0) & 0x00ff;
                    break;
                case 1:
                    memdata = (twincobr_txram_r.handler(0) & 0xff00) >> 8;
                    break;
                case 2:
                    memdata = twincobr_bgram_r.handler(0) & 0x00ff;
                    break;
                case 3:
                    memdata = (twincobr_bgram_r.handler(0) & 0xff00) >> 8;
                    break;
                case 4:
                    memdata = twincobr_fgram_r.handler(0) & 0x00ff;
                    break;
                case 5:
                    memdata = (twincobr_fgram_r.handler(0) & 0xff00) >> 8;
                    break;
            }
            return memdata;
        }
    };

    public static WriteHandlerPtr wardner_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int memdata = 0;
            switch (offset) {
                case 0:
                    memdata = twincobr_txram_r.handler(0) & 0xff00;
                    memdata |= data;
                    twincobr_txram_w.handler(0, memdata);
                    break;
                case 1:
                    memdata = twincobr_txram_r.handler(0) & 0x00ff;
                    memdata |= (data << 8);
                    twincobr_txram_w.handler(0, memdata);
                    break;
                case 2:
                    memdata = twincobr_bgram_r.handler(0) & 0xff00;
                    memdata |= data;
                    twincobr_bgram_w.handler(0, memdata);
                    break;
                case 3:
                    memdata = twincobr_bgram_r.handler(0) & 0x00ff;
                    memdata |= (data << 8);
                    twincobr_bgram_w.handler(0, memdata);
                    break;
                case 4:
                    memdata = twincobr_fgram_r.handler(0) & 0xff00;
                    memdata |= data;
                    twincobr_fgram_w.handler(0, memdata);
                    break;
                case 5:
                    memdata = twincobr_fgram_r.handler(0) & 0x00ff;
                    memdata |= (data << 8);
                    twincobr_fgram_w.handler(0, memdata);
                    break;
            }
        }
    };

    static void twincobr_draw_sprites(osd_bitmap bitmap, int priority) {
        int offs;

        if (toaplan_main_cpu == 0) /* 68k */ {
            /*TODO*///			for (offs = 0;offs < spriteram_size;offs += 8)
/*TODO*///			{
/*TODO*///				int attribute,sx,sy,flipx,flipy;
/*TODO*///				int sprite, color;
/*TODO*///	
/*TODO*///				attribute = READ_WORD(&buffered_spriteram[offs + 2]);
/*TODO*///				if ((attribute & 0x0c00) == priority) {	/* low priority */
/*TODO*///					sy = READ_WORD(&buffered_spriteram[offs + 6]) >> 7;
/*TODO*///					if (sy != 0x0100) {		/* sx = 0x01a0 or 0x0040*/
/*TODO*///						sprite = READ_WORD(&buffered_spriteram[offs]) & 0x7ff;
/*TODO*///						color  = attribute & 0x3f;
/*TODO*///						sx = READ_WORD(&buffered_spriteram[offs + 4]) >> 7;
/*TODO*///						flipx = attribute & 0x100;
/*TODO*///						if (flipx != 0) sx -= 14;		/* should really be 15 */
/*TODO*///						flipy = attribute & 0x200;
/*TODO*///						drawgfx(bitmap,Machine.gfx[3],
/*TODO*///							sprite,
/*TODO*///							color,
/*TODO*///							flipx,flipy,
/*TODO*///							sx-32,sy-16,
/*TODO*///							&Machine.visible_area,TRANSPARENCY_PEN,0);
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
        } else /* Z80 */ {
            for (offs = 0; offs < spriteram_size[0]; offs += 8) {
                int attribute, sx, sy, flipx, flipy;
                int sprite, color;

                attribute = buffered_spriteram.READ_WORD(offs + 2);//READ_WORD_Z80(&buffered_spriteram[offs + 2]);
                if ((attribute & 0x0c00) == priority) {
                    /* low priority */
                    sy = buffered_spriteram.READ_WORD(offs + 6) >> 7;//READ_WORD_Z80(&buffered_spriteram[offs + 6]) >> 7;
                    if (sy != 0x0100) {
                        /* sx = 0x01a0 or 0x0040*/
                        sprite = buffered_spriteram.READ_WORD(offs) & 0x7ff;//READ_WORD_Z80(&buffered_spriteram[offs]) & 0x7ff;
                        color = attribute & 0x3f;
                        sx = buffered_spriteram.READ_WORD(offs + 4) >> 7;//READ_WORD_Z80(&buffered_spriteram[offs + 4]) >> 7;
                        flipx = attribute & 0x100;
                        if (flipx != 0) {
                            sx -= 14;		/* should really be 15 */
                        }
                        flipy = attribute & 0x200;
                        drawgfx(bitmap, Machine.gfx[3],
                                sprite,
                                color,
                                flipx, flipy,
                                sx - 32, sy - 16,
                                Machine.visible_area, TRANSPARENCY_PEN, 0);
                    }
                }
            }
        }
    }

    static int offs, code, tile, pal_base, sprite, color;
    static int[] colmask = new int[64];
    public static VhUpdatePtr twincobr_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {

            if (twincobr_display_on != 0) {
                memset(palette_used_colors, PALETTE_COLOR_UNUSED, Machine.drv.total_colors);
                {
                    pal_base = Machine.drv.gfxdecodeinfo[2].color_codes_start;

                    for (color = 0; color < 16; color++) {
                        colmask[color] = 0;
                    }

                    for (offs = (twincobr_bgvideoram_size[0] >> 1) - 2; offs >= 0; offs -= 2) {
                        code = twincobr_bgvideoram.READ_WORD(offs + twincobr_bg_ram_bank);
                        tile = (code & 0x0fff);
                        color = (code & 0xf000) >> 12;
                        colmask[color] |= Machine.gfx[2].pen_usage[tile];
                    }

                    for (color = 0; color < 16; color++) {
                        for (int i = 0; i < 16; i++) {
                            if ((colmask[color] & (1 << i)) != 0) {
                                palette_used_colors.write(pal_base + 16 * color + i, PALETTE_COLOR_USED);
                            }
                        }
                    }

                    pal_base = Machine.drv.gfxdecodeinfo[1].color_codes_start;

                    for (color = 0; color < 16; color++) {
                        colmask[color] = 0;
                    }

                    scroll_x = (twincobr_flip_x_base + fgscrollx) & 0x01ff;
                    scroll_y = (twincobr_flip_y_base + fgscrolly) & 0x01ff;
                    vidbaseaddr = ((scroll_y >> 3) * 64) + (scroll_x >> 3);
                    scroll_realign_x = scroll_x >> 3;
                    for (offs = (31 * 41) - 1; offs >= 0; offs--) {
                        int u8_sx, u8_sy;
                        int u16_vidramaddr = 0;

                        u8_sx = (offs % 41) & 0xFF;
                        u8_sy = (offs / 41) & 0xFF;
                        u16_vidramaddr = ((vidbaseaddr + (u8_sy * 64) + u8_sx) * 2) & 0xFFFF;

                        if ((scroll_realign_x + u8_sx) > 63) {
                            u16_vidramaddr = (u16_vidramaddr - 128) & 0xFFFF;
                        }

                        code = twincobr_fgvideoram.READ_WORD(u16_vidramaddr & 0x1fff);
                        tile = (code & 0x0fff) | twincobr_fg_rom_bank;
                        color = (code & 0xf000) >> 12;
                        colmask[color] |= Machine.gfx[1].pen_usage[tile];
                    }

                    for (color = 0; color < 16; color++) {
                        if ((colmask[color] & (1 << 0)) != 0) {
                            palette_used_colors.write(pal_base + 16 * color, PALETTE_COLOR_TRANSPARENT);
                        }
                        for (int i = 1; i < 16; i++) {
                            if ((colmask[color] & (1 << i)) != 0) {
                                palette_used_colors.write(pal_base + 16 * color + i, PALETTE_COLOR_USED);
                            }
                        }
                    }

                    pal_base = Machine.drv.gfxdecodeinfo[3].color_codes_start;

                    for (color = 0; color < 64; color++) {
                        colmask[color] = 0;
                    }

                    if (toaplan_main_cpu == 0) /* 68k */ {
                        /*TODO*///			for (offs = 0;offs < spriteram_size[0];offs += 8)
/*TODO*///			{
/*TODO*///				int sy;
/*TODO*///				sy = READ_WORD(&buffered_spriteram[offs + 6]);
/*TODO*///				if (sy != 0x8000) {					/* Is sprite is turned off ? */
/*TODO*///					sprite = READ_WORD(&buffered_spriteram[offs]) & 0x7ff;
/*TODO*///					color = READ_WORD(&buffered_spriteram[offs + 2]) & 0x3f;
/*TODO*///					colmask[color] |= Machine.gfx[3].pen_usage[sprite];
/*TODO*///				}
/*TODO*///			}
                    } else /* Z80 */ {
                        for (offs = 0; offs < spriteram_size[0]; offs += 8) {
                            int sy;
                            sy = buffered_spriteram.READ_WORD(offs + 6);//sy = READ_WORD_Z80(&buffered_spriteram[offs + 6]);
                            if (sy != 0x8000) {
                                /* Is sprite is turned off ? */
                                sprite = buffered_spriteram.READ_WORD(offs) & 0x7ff;//sprite = READ_WORD_Z80(&buffered_spriteram[offs]) & 0x7ff;
                                color = buffered_spriteram.READ_WORD(offs + 2) & 0x3f;//color = READ_WORD_Z80(&buffered_spriteram.READ_WORD[offs + 2]) & 0x3f;
                                colmask[color] |= Machine.gfx[3].pen_usage[sprite];
                            }
                        }
                    }

                    for (color = 0; color < 64; color++) {
                        if ((colmask[color] & (1 << 0)) != 0) {
                            palette_used_colors.write(pal_base + 16 * color, PALETTE_COLOR_TRANSPARENT);
                        }
                        for (int i = 1; i < 16; i++) {
                            if ((colmask[color] & (1 << i)) != 0) {
                                palette_used_colors.write(pal_base + 16 * color + i, PALETTE_COLOR_USED);
                            }
                        }
                    }

                    pal_base = Machine.drv.gfxdecodeinfo[0].color_codes_start;

                    for (color = 0; color < 32; color++) {
                        colmask[color] = 0;
                    }

                    scroll_x = (twincobr_flip_x_base + txscrollx) & 0x01ff;
                    scroll_y = (twincobr_flip_y_base + txscrolly) & 0x00ff;
                    vidbaseaddr = ((scroll_y >> 3) * 64) + (scroll_x >> 3);
                    scroll_realign_x = scroll_x >> 3;
                    for (offs = (31 * 41) - 1; offs >= 0; offs--) {
                        int u8_sx, u8_sy;
                        int u16_vidramaddr = 0;

                        u8_sx = (offs % 41) & 0xFF;
                        u8_sy = (offs / 41) & 0xFF;

                        u16_vidramaddr = ((vidbaseaddr + (u8_sy * 64) + u8_sx) * 2) & 0xFFFF;
                        if ((scroll_realign_x + u8_sx) > 63) {
                            u16_vidramaddr = (u16_vidramaddr - 128) & 0xFFFF;
                        }
                        code = videoram.READ_WORD(u16_vidramaddr & 0x0fff);
                        tile = (code & 0x07ff);
                        color = (code & 0xf800) >> 11;
                        colmask[color] |= Machine.gfx[0].pen_usage[tile];
                    }

                    for (color = 0; color < 32; color++) {
                        if ((colmask[color] & (1 << 0)) != 0) {
                            palette_used_colors.write(pal_base + 8 * color, PALETTE_COLOR_TRANSPARENT);
                        }
                        for (int i = 1; i < 8; i++) {
                            if ((colmask[color] & (1 << i)) != 0) {
                                palette_used_colors.write(pal_base + 8 * color + i, PALETTE_COLOR_USED);
                            }
                        }
                    }

                    if (palette_recalc() != null) {
                        memset(dirtybuffer, 1, twincobr_bgvideoram_size[0] >> 1);
                    }
                }

                /* draw the background */
                for (offs = (twincobr_bgvideoram_size[0] >> 1) - 2; offs >= 0; offs -= 2) {
                    if (dirtybuffer[offs / 2] != 0) {
                        int sx, sy;

                        dirtybuffer[offs / 2] = 0;

                        sx = (offs / 2) % 64;
                        sy = (offs / 2) / 64;

                        code = twincobr_bgvideoram.READ_WORD(offs + twincobr_bg_ram_bank);
                        tile = (code & 0x0fff);
                        color = (code & 0xf000) >> 12;
                        if (twincobr_flip_screen != 0) {
                            sx = 63 - sx;
                            sy = 63 - sy;
                        }
                        drawgfx(tmpbitmap, Machine.gfx[2],
                                tile,
                                color,
                                twincobr_flip_screen, twincobr_flip_screen,
                                8 * sx, 8 * sy,
                                null, TRANSPARENCY_NONE, 0);
                    }
                }

                /* copy the background graphics */
                {
                    if (twincobr_flip_screen != 0) {
                        scroll_x = (twincobr_flip_x_base + bgscrollx + 0x141) & 0x1ff;
                        scroll_y = (twincobr_flip_y_base + bgscrolly + 0xf1) & 0x1ff;
                    } else {
                        scroll_x = (0x1c9 - bgscrollx) & 0x1ff;
                        scroll_y = (-0x1e - bgscrolly) & 0x1ff;
                    }
                    copyscrollbitmap(bitmap, tmpbitmap, 1, new int[]{scroll_x}, 1, new int[]{scroll_y}, Machine.visible_area, TRANSPARENCY_NONE, 0);
                }

                /* draw the sprites in low priority (Twin Cobra tanks under roofs) */
                twincobr_draw_sprites(bitmap, 0x0400);

                /* draw the foreground */
                scroll_x = (twincobr_flip_x_base + fgscrollx) & 0x01ff;
                scroll_y = (twincobr_flip_y_base + fgscrolly) & 0x01ff;
                vidbaseaddr = ((scroll_y >> 3) * 64) + (scroll_x >> 3);
                scroll_realign_x = scroll_x >> 3;
                /* realign video ram pointer */
                for (offs = (31 * 41) - 1; offs >= 0; offs--) {
                    int xpos, ypos;
                    int u8_sx, u8_sy;
                    int u16_vidramaddr = 0;

                    u8_sx = (offs % 41) & 0xFF;
                    u8_sy = (offs / 41) & 0xFF;

                    u16_vidramaddr = ((vidbaseaddr + (u8_sy * 64) + u8_sx) * 2) & 0xFFFF;
                    if ((scroll_realign_x + u8_sx) > 63) {
                        u16_vidramaddr = (u16_vidramaddr - 128) & 0xFFFF;
                    }

                    code = twincobr_fgvideoram.READ_WORD(u16_vidramaddr & 0x1fff);
                    tile = (code & 0x0fff) | twincobr_fg_rom_bank;
                    color = (code & 0xf000) >> 12;
                    if (twincobr_flip_screen != 0) {
                        u8_sx = 40 - u8_sx;
                        u8_sy = 30 - u8_sy;
                        xpos = (u8_sx * 8) - (7 - (scroll_x & 7));
                        ypos = (u8_sy * 8) - (7 - (scroll_y & 7));
                    } else {
                        xpos = (u8_sx * 8) - (scroll_x & 7);
                        ypos = (u8_sy * 8) - (scroll_y & 7);
                    }
                    drawgfx(bitmap, Machine.gfx[1],
                            tile,
                            color,
                            twincobr_flip_screen, twincobr_flip_screen,
                            xpos, ypos,
                            Machine.visible_area, TRANSPARENCY_PEN, 0);
                }

                /*TODO*///	/*********  Begin ugly sprite hack for Wardner when hero is in shop *********/
/*TODO*///		if ((wardner_sprite_hack) && (fgscrollx != bgscrollx)) {	/* Wardner ? */
/*TODO*///			if ((fgscrollx==0x1c9) || (twincobr_flip_screen && (fgscrollx==0x17a))) {	/* in the shop ? */
/*TODO*///				int wardner_hack = READ_WORD_Z80(&buffered_spriteram[0x0b04]);
/*TODO*///			/* sprite position 0x6300 to 0x8700 -- hero on shop keeper (normal) */
/*TODO*///			/* sprite position 0x3900 to 0x5e00 -- hero on shop keeper (flip) */
/*TODO*///				if ((wardner_hack > 0x3900) && (wardner_hack < 0x8700)) {	/* hero at shop keeper ? */
/*TODO*///						wardner_hack = READ_WORD_Z80(&buffered_spriteram[0x0b02]);
/*TODO*///						wardner_hack |= 0x0400;			/* make hero top priority */
/*TODO*///						WRITE_WORD_Z80(&buffered_spriteram[0x0b02],wardner_hack);
/*TODO*///						wardner_hack = READ_WORD_Z80(&buffered_spriteram[0x0b0a]);
/*TODO*///						wardner_hack |= 0x0400;
/*TODO*///						WRITE_WORD_Z80(&buffered_spriteram[0x0b0a],wardner_hack);
/*TODO*///						wardner_hack = READ_WORD_Z80(&buffered_spriteram[0x0b12]);
/*TODO*///						wardner_hack |= 0x0400;
/*TODO*///						WRITE_WORD_Z80(&buffered_spriteram[0x0b12],wardner_hack);
/*TODO*///						wardner_hack = READ_WORD_Z80(&buffered_spriteram[0x0b1a]);
/*TODO*///						wardner_hack |= 0x0400;
/*TODO*///						WRITE_WORD_Z80(&buffered_spriteram[0x0b1a],wardner_hack);
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	/**********  End ugly sprite hack for Wardner when hero is in shop **********/
/*TODO*///	
                /* draw the sprites in normal priority */
                twincobr_draw_sprites(bitmap, 0x0800);

                /* draw the top layer */
                scroll_x = (twincobr_flip_x_base + txscrollx) & 0x01ff;
                scroll_y = (twincobr_flip_y_base + txscrolly) & 0x00ff;
                vidbaseaddr = ((scroll_y >> 3) * 64) + (scroll_x >> 3);
                scroll_realign_x = scroll_x >> 3;
                for (offs = (31 * 41) - 1; offs >= 0; offs--) {
                    int xpos, ypos;
                    int u8_sx, u8_sy;
                    int u16_vidramaddr = 0;

                    u8_sx = (offs % 41) & 0xFF;
                    u8_sy = (offs / 41) & 0xFF;

                    u16_vidramaddr = (vidbaseaddr + (u8_sy * 64) + u8_sx) * 2;
                    if ((scroll_realign_x + u8_sx) > 63) {
                        u16_vidramaddr = (u16_vidramaddr - 128) & 0xFFFF;
                    }

                    code = videoram.READ_WORD(u16_vidramaddr & 0x0fff);
                    tile = (code & 0x07ff);
                    color = (code & 0xf800) >> 11;
                    if (twincobr_flip_screen != 0) {
                        u8_sx = 40 - u8_sx;
                        u8_sy = 30 - u8_sy;
                        xpos = (u8_sx * 8) - (7 - (scroll_x & 7));
                        ypos = (u8_sy * 8) - (7 - (scroll_y & 7));
                    } else {
                        xpos = (u8_sx * 8) - (scroll_x & 7);
                        ypos = (u8_sy * 8) - (scroll_y & 7);
                    }
                    drawgfx(bitmap, Machine.gfx[0],
                            tile,
                            color,
                            twincobr_flip_screen, twincobr_flip_screen,
                            xpos, ypos,
                            Machine.visible_area, TRANSPARENCY_PEN, 0);
                }

                /* draw the sprites in high priority */
                twincobr_draw_sprites(bitmap, 0x0c00);

            }
        }
    };

    public static VhEofCallbackPtr twincobr_eof_callback = new VhEofCallbackPtr() {
        public void handler() {
            /*  Spriteram is always 1 frame ahead, suggesting spriteram buffering.
			There are no CPU output registers that control this so we
			assume it happens automatically every frame, at the end of vblank */
            buffer_spriteram_w.handler(0, 0);
        }
    };

}
