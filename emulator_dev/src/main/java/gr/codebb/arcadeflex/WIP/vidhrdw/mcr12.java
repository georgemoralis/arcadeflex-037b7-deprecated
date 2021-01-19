/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.machine.mcr.*;
import static gr.codebb.arcadeflex.WIP.vidhrdw.mcr3.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;

public class mcr12 {

    public static /*UINT8*/ int last_cocktail_flip;
    public static /*UINT8 */ char[] spritebitmap;
    public static /*UINT32*/ int spritebitmap_width;
    public static /*UINT32*/ int spritebitmap_height;

    public static int /*INT8*/ mcr12_sprite_xoffs;
    public static int /*INT8*/ mcr12_sprite_xoffs_flip;

    public static /*UINT8*/ int xtiles, ytiles;

    /**
     * ***********************************
     *
     * Common video startup/shutdown
     *
     ************************************
     */
    public static VhStartPtr mcr12_vh_start = new VhStartPtr() {
        public int handler() {
            GfxElement gfx = Machine.gfx[1];

            /* allocate a temporary bitmap for the sprite rendering */
            spritebitmap_width = Machine.drv.screen_width + 2 * 32;
            spritebitmap_height = Machine.drv.screen_height + 2 * 32;
            spritebitmap = new char[spritebitmap_width * spritebitmap_height];
            if (spritebitmap == null) {
                return 1;
            }
            memset(spritebitmap, 0, spritebitmap_width * spritebitmap_height);

            /* if we're swapped in X/Y, the sprite data will be swapped */
 /* but that's not what we want, so we swap it back here */
            if (gfx != null && (Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
                UBytePtr base = new UBytePtr(gfx.gfxdata);
                int c, x, y;
                for (c = 0; c < gfx.total_elements; c++) {
                    for (y = 0; y < gfx.height; y++) {
                        for (x = y; x < gfx.width; x++) {
                            int temp = base.read(y * gfx.line_modulo + x);
                            base.write(y * gfx.line_modulo + x, base.read(x * gfx.line_modulo + y));
                            base.write(x * gfx.line_modulo + y, temp);
                        }
                    }
                    base.inc(gfx.char_modulo);
                }
            }

            /* compute tile counts */
            xtiles = Machine.drv.screen_width / 16;
            ytiles = Machine.drv.screen_height / 16;
            last_cocktail_flip = 0;

            /* start up the generic system */
            if (generic_vh_start.handler() != 0) {
                spritebitmap = null;
                return 1;
            }
            return 0;
        }
    };

    public static VhStopPtr mcr12_vh_stop = new VhStopPtr() {
        public void handler() {
            generic_vh_stop.handler();

            if (spritebitmap != null) {
                spritebitmap = null;
            }
        }
    };

    /**
     * ***********************************
     *
     * MCR2 palette writes
     *
     ************************************
     */
    public static WriteHandlerPtr mcr2_paletteram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int r, g, b;

            paletteram.write(offset, data);

            /* bit 2 of the red component is taken from bit 0 of the address */
            r = ((offset & 1) << 2) + (data >> 6);
            g = (data >> 0) & 7;
            b = (data >> 3) & 7;

            /* up to 8 bits */
            r = (r << 5) | (r << 2) | (r >> 1);
            g = (g << 5) | (g << 2) | (g >> 1);
            b = (b << 5) | (b << 2) | (b >> 1);

            palette_change_color(offset / 2, r, g, b);
        }
    };

    /**
     * ***********************************
     *
     * Videoram writes
     *
     ************************************
     */
    public static WriteHandlerPtr mcr1_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (videoram.read(offset) != data) {
                dirtybuffer[offset] = 1;
                videoram.write(offset, data);
            }
        }
    };

    public static WriteHandlerPtr mcr2_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (videoram.read(offset) != data) {
                dirtybuffer[offset & ~1] = 1;
                videoram.write(offset, data);
            }
        }
    };

    /**
     * ***********************************
     *
     * Background updates
     *
     ************************************
     */
    static void mcr1_update_background(osd_bitmap bitmap) {
        int offs;

        /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
        for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
            int dirty = dirtybuffer[offs];
            if (dirty != 0) {
                int mx = offs % 32;
                int my = offs / 32;
                int sx = 16 * mx;
                int sy = 16 * my;

                int code = videoram.read(offs);

                /* adjust for cocktail mode */
                if (mcr_cocktail_flip != 0) {
                    sx = (xtiles - 1) * 16 - sx;
                    sy = (ytiles - 1) * 16 - sy;
                }

                /* draw the tile */
                drawgfx(bitmap, Machine.gfx[0], code, 0, mcr_cocktail_flip, mcr_cocktail_flip,
                        sx, sy, Machine.visible_area, TRANSPARENCY_NONE, 0);

                /* if there's live sprite data here, draw the sprite data */
                if ((dirty & 2) != 0) {
                    /* draw the sprite */
                    if (bitmap.depth == 8) {
                        render_sprite_tile_8(bitmap, Machine.pens, 16, sx, sy);
                    }
                    /*TODO*///					else
/*TODO*///						render_sprite_tile_16(bitmap, &Machine.pens[16], sx, sy);
                }

                /* shift off the low bit of the dirty buffer */
                dirtybuffer[offs] = (char) (dirty >> 1);
            }
        }
    }

    static void mcr2_update_background(osd_bitmap bitmap, int check_sprites) {
        int offs;

        /* for every character in the Video RAM, check if it has been modified */
 /* since last time and update it accordingly. */
        for (offs = videoram_size[0] - 2; offs >= 0; offs -= 2) {
            int dirty = dirtybuffer[offs];
            if (dirty != 0) {
                int mx = (offs / 2) % 32;
                int my = (offs / 2) / 32;
                int sx = 16 * mx;
                int sy = 16 * my;

                int attr = videoram.read(offs + 1);
                int code = videoram.read(offs) + 256 * (attr & 0x01);
                int hflip = attr & 0x02;
                int vflip = attr & 0x04;
                int color = (attr & 0x18) >> 3;

                /* adjust for cocktail mode */
                if (mcr_cocktail_flip != 0) {
                    sx = (xtiles - 1) * 16 - sx;
                    sy = (ytiles - 1) * 16 - sy;
                    hflip = NOT(hflip);
                    vflip = NOT(vflip);
                }

                /* draw the tile */
                drawgfx(bitmap, Machine.gfx[0], code, color, hflip, vflip,
                        sx, sy, Machine.visible_area, TRANSPARENCY_NONE, 0);

                /* if there's live sprite data here, draw the sprite data */
                if (check_sprites != 0 && (dirty & 2) != 0) {
                    color = (attr & 0xc0) >> 6;

                    /* draw the sprite */
                    if (bitmap.depth == 8) {
                        render_sprite_tile_8(bitmap, Machine.pens, color * 16, sx, sy);
                    }
                    /*TODO*///					else
/*TODO*///						render_sprite_tile_16(bitmap, &Machine.pens[color * 16], sx, sy);
                }

                /* shift off the low bit of the dirty buffer */
                dirtybuffer[offs] = (char) (dirty >> 1);
            }
        }
    }

    /**
     * ***********************************
     *
     * Common sprite update
     *
     ************************************
     */
    static void mcr12_update_sprites(int scale) {
        int offs;

        /* render the sprites into the bitmap, ORing together */
        for (offs = 0; offs < spriteram_size[0]; offs += 4) {
            int code, x, y, sx, sy, xcount, ycount, xtile, ytile, hflip, vflip;

            /* skip if zero */
            if (spriteram.read(offs) == 0) {
                continue;
            }

            /* extract the bits of information */
            code = spriteram.read(offs + 1) & 0x3f;
            hflip = spriteram.read(offs + 1) & 0x40;
            vflip = spriteram.read(offs + 1) & 0x80;
            x = (spriteram.read(offs + 2) - 4) * 2;
            y = (240 - spriteram.read(offs)) * 2;

            /* apply cocktail mode */
            if (mcr_cocktail_flip != 0) {
                hflip = NOT(hflip);
                vflip = NOT(vflip);
                x = 466 - x + mcr12_sprite_xoffs_flip;
                y = 450 - y;
            } else {
                x += mcr12_sprite_xoffs;
            }

            /* wrap and clip */
            if (x > Machine.visible_area.max_x) {
                x -= 512;
            }
            if (y > Machine.visible_area.max_y) {
                y -= 512;
            }
            if (x <= -32 || y <= -32) {
                continue;
            }

            /* draw the sprite into the sprite bitmap */
            render_one_sprite(code, x + 32, y + 32, hflip, vflip);

            /* determine which tiles we will overdraw with this sprite */
            sx = x / 16;
            sy = y / 16;
            xcount = (x & 15) != 0 ? 3 : 2;
            ycount = (y & 15) != 0 ? 3 : 2;

            /* loop over dirty tiles and set the sprite bit */
            for (ytile = sy; ytile < sy + ycount; ytile++) {
                for (xtile = sx; xtile < sx + xcount; xtile++) {
                    if (xtile >= 0 && xtile < xtiles && ytile >= 0 && ytile < ytiles) {
                        int off;
                        if (mcr_cocktail_flip == 0) {
                            off = 32 * ytile + xtile;
                        } else {
                            off = 32 * (ytiles - 1 - ytile) + (xtiles - 1 - xtile);
                        }
                        dirtybuffer[off << scale] |= 2;
                    }
                }
            }
        }
    }

    /**
     * ***********************************
     *
     * Main refresh routines
     *
     ************************************
     */
    public static VhUpdatePtr mcr1_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            /* mark everything dirty on a full refresh or cocktail flip change */
            if (palette_recalc() != null || full_refresh != 0 || last_cocktail_flip != mcr_cocktail_flip) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }
            last_cocktail_flip = mcr_cocktail_flip;

            /* update the sprites */
            mcr12_update_sprites(0);

            /* redraw everything, merging the bitmaps */
            mcr1_update_background(bitmap);
        }
    };

    public static VhUpdatePtr mcr2_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            /* mark everything dirty on a full refresh or cocktail flip change */
            if (palette_recalc() != null || full_refresh != 0 || last_cocktail_flip != mcr_cocktail_flip) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }
            last_cocktail_flip = mcr_cocktail_flip;

            /* update the sprites */
            mcr12_update_sprites(1);

            /* redraw everything, merging the bitmaps */
            mcr2_update_background(bitmap, 1);
        }
    };

    /**
     * ***********************************
     *
     * Journey-specific MCR2 redraw
     *
     * Uses the MCR3 sprite drawing
     *
     ************************************
     */
    public static VhUpdatePtr journey_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            /* mark everything dirty on a cocktail flip change */
            if (palette_recalc() != null || last_cocktail_flip != mcr_cocktail_flip) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }
            last_cocktail_flip = mcr_cocktail_flip;

            /* redraw the background */
            mcr2_update_background(tmpbitmap, 0);

            /* copy it to the destination */
            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);

            /* draw the sprites */
            mcr3_update_sprites(bitmap, 0x03, 0, 0, 0);
        }
    };

    /**
     * ***********************************
     *
     * Sprite drawing
     *
     ************************************
     */
    static void render_one_sprite(int code, int sx, int sy, int hflip, int vflip) {
        GfxElement gfx = Machine.gfx[1];
        UBytePtr src = new UBytePtr(gfx.gfxdata, gfx.char_modulo * code);
        int y, x;

        /* adjust for vflip */
        if (vflip != 0) {
            src.inc(31 * gfx.line_modulo);
        }

        /* loop over lines in the sprite */
        for (y = 0; y < 32; y++, sy++) {
            UBytePtr dst = new UBytePtr(spritebitmap, spritebitmap_width * sy + sx);

            /* redraw the line */
            if (hflip == 0) {
                for (x = 0; x < 32; x++) {
                    dst.writeinc(dst.read() | src.readinc());
                }
            } else {
                src.inc(32);
                for (x = 0; x < 32; x++) {
                    dst.writeinc(dst.read() | src.readdec());
                }
                src.inc(32);
            }

            /* adjust for vflip */
            if (vflip != 0) {
                src.dec(2 * gfx.line_modulo);
            }
        }
    }

    /*TODO*///	
/*TODO*///	
/*TODO*///	/*************************************
/*TODO*///	 *
/*TODO*///	 *		Depth-specific refresh
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
/*TODO*///	#define DRAW_FUNC render_sprite_tile_8
/*TODO*///	#define TYPE UINT8
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	#define DRAW_FUNC render_sprite_tile_16
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
/*TODO*///	 *		Core refresh routine
/*TODO*///	 *
/*TODO*///	 *************************************/
    public static void render_sprite_tile_8(osd_bitmap bitmap, char[] pens, int offset, int sx, int sy) {
        int orientation = Machine.orientation;
        int x, y;

        /* draw any dirty scanlines from the VRAM directly */
        for (y = 0; y < 16; y++, sy++) {
            UBytePtr src = new UBytePtr(spritebitmap, (sy + 32) * spritebitmap_width + (sx + 32));
            UBytePtr dst = new UBytePtr(bitmap.line[sy], sx);
            int xadv = 1;

            /* adjust in case we're oddly oriented */
            //ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, sx, sy, xadv);
            if (orientation != 0) {
                int dy = bitmap.line[1].offset - bitmap.line[0].offset;
                int tx = sx, ty = sy, temp;
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

            /* redraw the sprite scanline, erasing as we go */
            for (x = 0; x < 16; x++, dst.offset += xadv) {
                int pixel = src.read();
                if ((pixel & 7) != 0) {
                    dst.write(pens[offset + pixel]);
                }
                src.writeinc(0);
            }
        }
    }
    /*TODO*///	
    /*TODO*///	void DRAW_FUNC(struct osd_bitmap *bitmap, const UINT16 *pens, int sx, int sy)
/*TODO*///	{
/*TODO*///		int orientation = Machine.orientation;
/*TODO*///		int x, y;
/*TODO*///	
/*TODO*///		/* draw any dirty scanlines from the VRAM directly */
/*TODO*///		for (y = 0; y < 16; y++, sy++)
/*TODO*///		{
/*TODO*///			UINT8 *src = &spritebitmap[(sy + 32) * spritebitmap_width + (sx + 32)];
/*TODO*///			TYPE *dst = &((TYPE *)bitmap.line[sy])[sx];
/*TODO*///			int xadv = 1;
/*TODO*///	
/*TODO*///			/* adjust in case we're oddly oriented */
/*TODO*///			ADJUST_FOR_ORIENTATION(orientation, bitmap, dst, sx, sy, xadv);
/*TODO*///	
/*TODO*///			/* redraw the sprite scanline, erasing as we go */
/*TODO*///			for (x = 0; x < 16; x++, dst += xadv)
/*TODO*///			{
/*TODO*///				int pixel = *src;
/*TODO*///				if ((pixel & 7) != 0)
/*TODO*///					*dst = pens[pixel];
/*TODO*///				*src++ = 0;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	#endif
}
