/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.drivers.balsente.u8_balsente_shooter;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.balsente.u8_balsente_shooter_x;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.balsente.u8_balsente_shooter_y;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.old.mame.drawgfx.plot_pixel;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;

public class balsente {

    /**
     * ***********************************
     *
     * Statics
     *
     ************************************
     */
    static UBytePtr local_videoram;
    static char[] scanline_dirty;
    static char[] scanline_palette;

    static char/*UINT8*/ u8_last_scanline_palette;
    static char/*UINT8*/ u8_screen_refresh_counter;
    static char/*UINT8*/ u8_palettebank_vis;

    public static VhStartPtr balsente_vh_start = new VhStartPtr() {
        public int handler() {
            /* reset the system */
            u8_palettebank_vis = 0;

            /* allocate a local copy of video RAM */
            local_videoram = new UBytePtr(256 * 256);
            if (local_videoram == null) {
                balsente_vh_stop.handler();
                return 1;
            }

            /* allocate a scanline dirty array */
            scanline_dirty = new char[256];
            if (scanline_dirty == null) {
                balsente_vh_stop.handler();
                return 1;
            }

            /* allocate a scanline palette array */
            scanline_palette = new char[256];
            if (scanline_palette == null) {
                balsente_vh_stop.handler();
                return 1;
            }

            /* mark everything dirty to start */
            memset(scanline_dirty, 1, 256);

            /* reset the scanline palette */
            memset(scanline_palette, 0, 256);
            u8_last_scanline_palette = 0;

            return 0;
        }
    };

    /**
     * ***********************************
     *
     * Video system shutdown
     *
     ************************************
     */
    public static VhStopPtr balsente_vh_stop = new VhStopPtr() {
        public void handler() {
            /* free the local video RAM array */
            if (local_videoram != null) {
                local_videoram = null;
            }

            /* free the scanline dirty array */
            if (scanline_dirty != null) {
                scanline_dirty = null;
            }

            /* free the scanline dirty array */
            if (scanline_palette != null) {
                scanline_palette = null;
            }
        }
    };

    /**
     * ***********************************
     *
     * Video RAM write
     *
     ************************************
     */
    public static WriteHandlerPtr balsente_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            videoram.write(offset, data);

            /* expand the two pixel values into two bytes */
            local_videoram.write(offset * 2 + 0, data >> 4);
            local_videoram.write(offset * 2 + 1, data & 15);

            /* mark the scanline dirty */
            scanline_dirty[offset / 128] = 1;
        }
    };

    /**
     * ***********************************
     *
     * Palette banking
     *
     ************************************
     */
    static void update_palette() {
        int scanline = cpu_getscanline(), i;
        if (scanline > 255) {
            scanline = 0;
        }

        /* special case: the scanline is the same as last time, but a screen refresh has occurred */
        if (scanline == u8_last_scanline_palette && u8_screen_refresh_counter != 0) {
            for (i = 0; i < 256; i++) {
                /* mark the scanline dirty if it was a different palette */
                if (scanline_palette[i] != u8_palettebank_vis) {
                    scanline_dirty[i] = 1;
                }
                scanline_palette[i] = u8_palettebank_vis;
            }
        } /* fill in the scanlines up till now */ else {
            for (i = u8_last_scanline_palette; i != scanline; i = (i + 1) & 255) {
                /* mark the scanline dirty if it was a different palette */
                if (scanline_palette[i] != u8_palettebank_vis) {
                    scanline_dirty[i] = 1;
                }
                scanline_palette[i] = u8_palettebank_vis;
            }

            /* remember where we left off */
            u8_last_scanline_palette = (char) ((scanline & 0xFF));
        }

        /* reset the screen refresh counter */
        u8_screen_refresh_counter = 0;
    }

    public static WriteHandlerPtr balsente_palette_select_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /* only update if changed */
            if (u8_palettebank_vis != (data & 3)) {
                /* update the scanline palette */
                update_palette();
                u8_palettebank_vis = (char) ((data & 3) & 0xFF);
            }

            logerror("balsente_palette_select_w(%d) scanline=%d\n", data & 3, cpu_getscanline());
        }
    };

    /**
     * ***********************************
     *
     * Palette RAM write
     *
     ************************************
     */
    public static WriteHandlerPtr balsente_paletteram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int r, g, b;

            paletteram.write(offset, data & 0x0f);

            r = paletteram.read((offset & ~3) + 0);
            g = paletteram.read((offset & ~3) + 1);
            b = paletteram.read((offset & ~3) + 2);
            palette_change_color(offset / 4, (r << 4) | r, (g << 4) | g, (b << 4) | b);
        }
    };

    /**
     * ***********************************
     *
     * Main screen refresh
     *
     ************************************
     */
    public static VhUpdatePtr balsente_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            char[] palette_used = new char[4];
            int x, y, i;

            /* update the remaining scanlines */
            u8_screen_refresh_counter++;
            update_palette();

            /* determine which palette banks were used */
            palette_used[0] = palette_used[1] = palette_used[2] = palette_used[3] = 0;
            for (i = 0; i < 240; i++) {
                palette_used[scanline_palette[i]] = 1;
            }

            /* make sure color 1024 is white for our crosshair */
            palette_change_color(1024, 0xff, 0xff, 0xff);

            /* set the used status of all the palette entries */
            for (x = 0; x < 4; x++) {
                if (palette_used[x] != 0) {
                    memset(palette_used_colors, x * 256, PALETTE_COLOR_USED, 256);
                } else {
                    memset(palette_used_colors, x * 256, PALETTE_COLOR_UNUSED, 256);
                }
            }
            palette_used_colors.write(1024, u8_balsente_shooter != 0 ? PALETTE_COLOR_USED : PALETTE_COLOR_UNUSED);

            /* recompute the palette, and mark all scanlines dirty if we need to redraw */
            if (palette_recalc() != null) {
                memset(scanline_dirty, 1, 256);
            }

            /* do the core redraw */
            if (bitmap.depth == 8) {
                update_screen_8(bitmap, full_refresh);
            } else {
                throw new UnsupportedOperationException("unsupported");
                //update_screen_16(bitmap, full_refresh);
            }

            /* draw a crosshair */
            if (u8_balsente_shooter != 0) {
                int beamx = u8_balsente_shooter_x;
                int beamy = u8_balsente_shooter_y - 12;

                int xoffs = beamx - 3;
                int yoffs = beamy - 3;

                for (y = -3; y <= 3; y++, yoffs++, xoffs++) {
                    if (yoffs >= 0 && yoffs < 240 && beamx >= 0 && beamx < 256) {
                        plot_pixel.handler(bitmap, beamx, yoffs, Machine.pens[1024]);
                        scanline_dirty[yoffs] = 1;
                    }
                    if (xoffs >= 0 && xoffs < 256 && beamy >= 0 && beamy < 240) {
                        plot_pixel.handler(bitmap, xoffs, beamy, Machine.pens[1024]);
                    }
                }
            }
        }
    };

    /**
     * ***********************************
     *
     * Core refresh routine
     *
     ************************************
     */
    public static void update_screen_8(osd_bitmap bitmap, int full_refresh) {
        int orientation = Machine.orientation;
        int x, y, i;

        /* draw any dirty scanlines from the VRAM directly */
        for (y = 0; y < 240; y++) {
            char[] pens = Machine.pens;
            int pens_offset = scanline_palette[y] * 256;
            if (scanline_dirty[y] != 0 || full_refresh != 0) {
                UBytePtr src = new UBytePtr(local_videoram, y * 256);
                UBytePtr dst = new UBytePtr(bitmap.line[y]);
                int xadv = 1;
                if (orientation != 0) {
                    /* adjust in case we're oddly oriented */
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
                for (x = 0; x < 256; x++, dst.offset += xadv) {
                    dst.write(pens[pens_offset + src.readinc()]);
                }
                scanline_dirty[y] = 0;
            }
        }

        /* draw the sprite images */
        for (i = 0; i < 40; i++) {
            UBytePtr sprite = new UBytePtr(spriteram, ((0xe0 + i * 4) & 0xff));
            UBytePtr src;
            int flags = sprite.read(0);
            int image = sprite.read(1) | ((flags & 3) << 8);
            int ypos = sprite.read(2) + 17;
            int xpos = sprite.read(3);

            /* get a pointer to the source image */
            src = new UBytePtr(memory_region(REGION_GFX1), 64 * image);
            if ((flags & 0x80) != 0) {
                src.offset += 4 * 15;
            }

            /* loop over y */
            for (y = 0; y < 16; y++, ypos = (ypos + 1) & 255) {
                if (ypos >= 16 && ypos < 240) {
                    char[] pens = Machine.pens;
                    int pens_offset = scanline_palette[y] * 256;
                    UBytePtr old = new UBytePtr(local_videoram, ypos * 256 + xpos);
                    UBytePtr dst = new UBytePtr(bitmap.line[ypos], xpos);
                    int currx = xpos, xadv = 1;

                    /* adjust in case we're oddly oriented */
                    if (orientation != 0) {
                        int dy = bitmap.line[1].offset - bitmap.line[0].offset;
                        int tx = xpos, ty = ypos, temp;
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
                    /* mark this scanline dirty */
                    scanline_dirty[ypos] = 1;

                    /* standard case */
                    if ((flags & 0x40) == 0) {
                        /* loop over x */
                        for (x = 0; x < 4; x++, dst.offset += xadv * 2, old.offset += 2) {
                            int ipixel = src.readinc();
                            int left = ipixel & 0xf0;
                            int right = (ipixel << 4) & 0xf0;
                            int pen;

                            /* left pixel */
                            if (left != 0 && currx >= 0 && currx < 256) {
                                /* combine with the background */
                                pen = left | old.read(0);
                                dst.write(0, pens[pens_offset + pen]);
                            }
                            currx++;

                            /* right pixel */
                            if (right != 0 && currx >= 0 && currx < 256) {
                                /* combine with the background */
                                pen = right | old.read(1);
                                dst.write(xadv, pens[pens_offset + pen]);
                            }
                            currx++;
                        }
                    } /* hflip case */ else {
                        src.offset += 4;

                        /* loop over x */
                        for (x = 0; x < 4; x++, dst.offset += xadv * 2, old.offset += 2) {
                            src.dec();
                            int ipixel = src.read();
                            int left = (ipixel << 4) & 0xf0;
                            int right = ipixel & 0xf0;
                            int pen;

                            /* left pixel */
                            if (left != 0 && currx >= 0 && currx < 256) {
                                /* combine with the background */
                                pen = left | old.read(0);
                                dst.write(0, pens[pens_offset + pen]);
                            }
                            currx++;

                            /* right pixel */
                            if (right != 0 && currx >= 0 && currx < 256) {
                                /* combine with the background */
                                pen = right | old.read(1);
                                dst.write(xadv, pens[pens_offset + pen]);
                            }
                            currx++;
                        }
                        src.offset += 4;
                    }
                } else {
                    src.offset += 4;
                }
                if ((flags & 0x80) != 0) {
                    src.offset -= 2 * 4;
                }
            }
        }
    }

    /*TODO*///        ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, 0, y, xadv)
/*TODO*///	#define ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, x, y, xadv)	
/*TODO*///		if (orientation != 0)													
/*TODO*///		{																	
/*TODO*///			int dy = bitmap.line[1].offset - bitmap.line[0].offset;						
/*TODO*///			int tx = x, ty = y, temp;										
/*TODO*///			if ((orientation & ORIENTATION_SWAP_XY) != 0)							
/*TODO*///			{																
/*TODO*///				temp = tx; tx = ty; ty = temp;								
/*TODO*///				xadv = dy / (bitmap.depth / 8);							
/*TODO*///			}																
/*TODO*///			if ((orientation & ORIENTATION_FLIP_X) != 0)							
/*TODO*///			{																
/*TODO*///				tx = bitmap.width - 1 - tx;								
/*TODO*///				if ((orientation & ORIENTATION_SWAP_XY)==0) xadv = -xadv;		
/*TODO*///			}																
/*TODO*///			if ((orientation & ORIENTATION_FLIP_Y) != 0)							
/*TODO*///			{																
/*TODO*///				ty = bitmap.height - 1 - ty;								
/*TODO*///				if ((orientation & ORIENTATION_SWAP_XY)) xadv = -xadv;		
/*TODO*///			}																
/*TODO*///			/* can't lookup line because it may be negative! */				
/*TODO*///			dst = (TYPE *)(bitmap.line[0] + dy * ty) + tx;					
/*TODO*///		}
/*TODO*///        
/*TODO*///        	void update_screen_16(struct osd_bitmap *bitmap, int full_refresh)
/*TODO*///	{
/*TODO*///		int orientation = Machine.orientation;
/*TODO*///		int x, y, i;
/*TODO*///	
/*TODO*///		/* draw any dirty scanlines from the VRAM directly */
/*TODO*///		for (y = 0; y < 240; y++)
/*TODO*///		{
/*TODO*///			UINT16 *pens = &Machine.pens[scanline_palette[y] * 256];
/*TODO*///			if (scanline_dirty[y] || full_refresh)
/*TODO*///			{
/*TODO*///				UINT8 *src = &local_videoram[y * 256];
/*TODO*///				TYPE *dst = (TYPE *)bitmap.line[y];
/*TODO*///				int xadv = 1;
/*TODO*///	
/*TODO*///				/* adjust in case we're oddly oriented */
/*TODO*///				ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, 0, y, xadv);
/*TODO*///	
/*TODO*///				/* redraw the scanline */
/*TODO*///				for (x = 0; x < 256; x++, dst += xadv)
/*TODO*///					*dst = pens[*src++];
/*TODO*///				scanline_dirty[y] = 0;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	
/*TODO*///		/* draw the sprite images */
/*TODO*///		for (i = 0; i < 40; i++)
/*TODO*///		{
/*TODO*///			UINT8 *sprite = spriteram + ((0xe0 + i * 4) & 0xff);
/*TODO*///			UINT8 *src;
/*TODO*///			int flags = sprite[0];
/*TODO*///			int image = sprite[1] | ((flags & 3) << 8);
/*TODO*///			int ypos = sprite[2] + 17;
/*TODO*///			int xpos = sprite[3];
/*TODO*///	
/*TODO*///			/* get a pointer to the source image */
/*TODO*///			src = &memory_region(REGION_GFX1)[64 * image];
/*TODO*///			if ((flags & 0x80) != 0) src += 4 * 15;
/*TODO*///	
/*TODO*///			/* loop over y */
/*TODO*///			for (y = 0; y < 16; y++, ypos = (ypos + 1) & 255)
/*TODO*///			{
/*TODO*///				if (ypos >= 16 && ypos < 240)
/*TODO*///				{
/*TODO*///					UINT16 *pens = &Machine.pens[scanline_palette[y] * 256];
/*TODO*///					UINT8 *old = &local_videoram[ypos * 256 + xpos];
/*TODO*///					TYPE *dst = &((TYPE *)bitmap.line[ypos])[xpos];
/*TODO*///					int currx = xpos, xadv = 1;
/*TODO*///	
/*TODO*///					/* adjust in case we're oddly oriented */
/*TODO*///					ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, xpos, ypos, xadv);
/*TODO*///	
/*TODO*///					/* mark this scanline dirty */
/*TODO*///					scanline_dirty[ypos] = 1;
/*TODO*///	
/*TODO*///					/* standard case */
/*TODO*///					if (!(flags & 0x40))
/*TODO*///					{
/*TODO*///						/* loop over x */
/*TODO*///						for (x = 0; x < 4; x++, dst += xadv * 2, old += 2)
/*TODO*///						{
/*TODO*///							int ipixel = *src++;
/*TODO*///							int left = ipixel & 0xf0;
/*TODO*///							int right = (ipixel << 4) & 0xf0;
/*TODO*///							int pen;
/*TODO*///	
/*TODO*///							/* left pixel */
/*TODO*///							if (left && currx >= 0 && currx < 256)
/*TODO*///							{
/*TODO*///								/* combine with the background */
/*TODO*///								pen = left | old[0];
/*TODO*///								dst[0] = pens[pen];
/*TODO*///							}
/*TODO*///							currx++;
/*TODO*///	
/*TODO*///							/* right pixel */
/*TODO*///							if (right && currx >= 0 && currx < 256)
/*TODO*///							{
/*TODO*///								/* combine with the background */
/*TODO*///								pen = right | old[1];
/*TODO*///								dst[xadv] = pens[pen];
/*TODO*///							}
/*TODO*///							currx++;
/*TODO*///						}
/*TODO*///					}
/*TODO*///	
/*TODO*///					/* hflip case */
/*TODO*///					else
/*TODO*///					{
/*TODO*///						src += 4;
/*TODO*///	
/*TODO*///						/* loop over x */
/*TODO*///						for (x = 0; x < 4; x++, dst += xadv * 2, old += 2)
/*TODO*///						{
/*TODO*///							int ipixel = *--src;
/*TODO*///							int left = (ipixel << 4) & 0xf0;
/*TODO*///							int right = ipixel & 0xf0;
/*TODO*///							int pen;
/*TODO*///	
/*TODO*///							/* left pixel */
/*TODO*///							if (left && currx >= 0 && currx < 256)
/*TODO*///							{
/*TODO*///								/* combine with the background */
/*TODO*///								pen = left | old[0];
/*TODO*///								dst[0] = pens[pen];
/*TODO*///							}
/*TODO*///							currx++;
/*TODO*///	
/*TODO*///							/* right pixel */
/*TODO*///							if (right && currx >= 0 && currx < 256)
/*TODO*///							{
/*TODO*///								/* combine with the background */
/*TODO*///								pen = right | old[1];
/*TODO*///								dst[xadv] = pens[pen];
/*TODO*///							}
/*TODO*///							currx++;
/*TODO*///						}
/*TODO*///						src += 4;
/*TODO*///					}
/*TODO*///				}
/*TODO*///				else
/*TODO*///					src += 4;
/*TODO*///				if ((flags & 0x80) != 0) src -= 2 * 4;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	#endif
}
