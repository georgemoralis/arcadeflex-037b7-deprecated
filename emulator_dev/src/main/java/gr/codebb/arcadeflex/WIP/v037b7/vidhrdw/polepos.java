/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.bitmap_alloc;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.bitmap_free;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.drawgfxzoom;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.readinputport;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.COMBINE_WORD;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.memoryH.COMBINE_WORD_MEM;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import gr.codebb.arcadeflex.common.SubArrays.UShortArray;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;

public class polepos {

    public static UBytePtr polepos_view_memory = new UBytePtr();
    public static UBytePtr polepos_road_memory = new UBytePtr();
    public static UBytePtr polepos_sprite_memory = new UBytePtr();
    public static UBytePtr polepos_alpha_memory = new UBytePtr();

    /* modified vertical position built from three nibbles (12 bit)
	 * of ROMs 136014-142, 136014-143, 136014-144
	 * The value RVP (road vertical position, lower 12 bits) is added
	 * to this value and the upper 10 bits of the result are used to
	 * address the playfield video memory (AB0 - AB9).
     */
    static char[] polepos_vertical_position_modifier = new char[256];

    static char view_hscroll;
    static char road_vscroll;

    public static UBytePtr road_control;
    public static UBytePtr road_bits1;
    public static UBytePtr road_bits2;

    static osd_bitmap view_bitmap;
    static char[] /*UINT8*/ u8_view_dirty;

    /**
     * *************************************************************************
     *
     * Convert the color PROMs into a more useable format.
     *
     * Pole Position has three 256x4 palette PROMs (one per gun) and a lot ;-)
     * of 256x4 lookup table PROMs. The palette PROMs are connected to the RGB
     * output this way:
     *
     * bit 3 -- 220 ohm resistor -- RED/GREEN/BLUE -- 470 ohm resistor --
     * RED/GREEN/BLUE -- 1 kohm resistor -- RED/GREEN/BLUE bit 0 -- 2.2kohm
     * resistor -- RED/GREEN/BLUE
     *
     **************************************************************************
     */
    public static VhConvertColorPromPtr polepos_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i, j;

            /**
             * *****************************************************
             * Color PROMs Sheet 15B: middle, 136014-137,138,139 Inputs: MUX0
             * ... MUX3, ALPHA/BACK, SPRITE/BACK, 128V, COMPBLANK
             *
             * Note that we only decode the lower 128 colors because the upper
             * 128 are all black and used during the horizontal and vertical
             * blanking periods.
             * *****************************************************
             */
            int p_ptr = 0;
            for (i = 0; i < 128; i++) {
                int bit0, bit1, bit2, bit3;

                /* Sheet 15B: 136014-0137 red component */
                bit0 = (color_prom.read(0x000 + i) >> 0) & 1;
                bit1 = (color_prom.read(0x000 + i) >> 1) & 1;
                bit2 = (color_prom.read(0x000 + i) >> 2) & 1;
                bit3 = (color_prom.read(0x000 + i) >> 3) & 1;
                palette[p_ptr] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);

                /* Sheet 15B: 136014-0138 green component */
                bit0 = (color_prom.read(0x100 + i) >> 0) & 1;
                bit1 = (color_prom.read(0x100 + i) >> 1) & 1;
                bit2 = (color_prom.read(0x100 + i) >> 2) & 1;
                bit3 = (color_prom.read(0x100 + i) >> 3) & 1;
                palette[p_ptr] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);

                /* Sheet 15B: 136014-0139 blue component */
                bit0 = (color_prom.read(0x200 + i) >> 0) & 1;
                bit1 = (color_prom.read(0x200 + i) >> 1) & 1;
                bit2 = (color_prom.read(0x200 + i) >> 2) & 1;
                bit3 = (color_prom.read(0x200 + i) >> 3) & 1;
                palette[p_ptr] = (char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3);
            }

            /**
             * *****************************************************
             * Alpha colors (colors 0x000-0x1ff) Sheet 15B: top left, 136014-140
             * Inputs: SHFT0, SHFT1 and CHA8* ... CHA13*
             * *****************************************************
             */
            for (i = 0; i < 64 * 4; i++) {
                int color = color_prom.read(0x300 + i);
                colortable[0x0000 + i] = (color != 15) ? (char) (0x020 + color) : 0;
                colortable[0x0100 + i] = (color != 15) ? (char) (0x060 + color) : 0;
            }

            /**
             * *****************************************************
             * View colors (colors 0x200-0x3ff) Sheet 13A: left, 136014-141
             * Inputs: SHFT2, SHFT3 and CHA8 ... CHA13
             * *****************************************************
             */
            for (i = 0; i < 64 * 4; i++) {
                int color = color_prom.read(0x400 + i);
                colortable[0x0200 + i] = (char) (0x000 + color);
                colortable[0x0300 + i] = (char) (0x040 + color);
            }

            /**
             * *****************************************************
             * Sprite colors (colors 0x400-0xbff) Sheet 14B: right, 136014-146
             * Inputs: CUSTOM0 ... CUSTOM3 and DATA0 ... DATA5
             * *****************************************************
             */
            for (i = 0; i < 64 * 16; i++) {
                int color = color_prom.read(0xc00 + i);
                colortable[0x0400 + i] = (color != 15) ? (char) (0x010 + color) : 0;
                colortable[0x0800 + i] = (color != 15) ? (char) (0x050 + color) : 0;
            }

            /**
             * *****************************************************
             * Road colors (colors 0xc00-0x13ff) Sheet 13A: bottom left,
             * 136014-145 Inputs: R1 ... R6 and CHA0 ... CHA3
             * *****************************************************
             */
            for (i = 0; i < 64 * 16; i++) {
                int color = color_prom.read(0x800 + i);
                colortable[0x0c00 + i] = (char) (0x000 + color);
                colortable[0x1000 + i] = (char) (0x040 + color);
            }

            /* 136014-142, 136014-143, 136014-144 Vertical position modifiers */
            for (i = 0; i < 256; i++) {
                j = color_prom.read(0x500 + i) + (color_prom.read(0x600 + i) << 4) + (color_prom.read(0x700 + i) << 8);
                polepos_vertical_position_modifier[i] = (char) j;
            }

            road_control = new UBytePtr(color_prom, 0x2000);
            road_bits1 = new UBytePtr(color_prom, 0x4000);
            road_bits2 = new UBytePtr(color_prom, 0x6000);
        }
    };
    /**
     * *************************************************************************
     *
     * Video initialization/shutdown
     *
     **************************************************************************
     */

    public static VhStartPtr polepos_vh_start = new VhStartPtr() {
        public int handler() {
            /* allocate view bitmap */
            view_bitmap = bitmap_alloc(64 * 8, 16 * 8);

            /* allocate view dirty buffer */
            u8_view_dirty = new char[64 * 16];

            return 0;
        }
    };

    public static VhStopPtr polepos_vh_stop = new VhStopPtr() {
        public void handler() {
            bitmap_free(view_bitmap);
            u8_view_dirty = null;
        }
    };

    /**
     * *************************************************************************
     *
     * Sprite memory
     *
     **************************************************************************
     */
    public static ReadHandlerPtr polepos_sprite_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_sprite_memory.READ_WORD(offset);
        }
    };

    public static WriteHandlerPtr polepos_sprite_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            COMBINE_WORD_MEM(polepos_sprite_memory, offset, data);
        }
    };

    public static ReadHandlerPtr polepos_z80_sprite_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_sprite_r.handler(offset << 1) & 0xff;
        }
    };

    public static WriteHandlerPtr polepos_z80_sprite_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            polepos_sprite_w.handler(offset << 1, data | 0xff000000);
        }
    };

    /**
     * *************************************************************************
     *
     * Road memory
     *
     **************************************************************************
     */
    public static ReadHandlerPtr polepos_road_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_road_memory.READ_WORD(offset);
        }
    };

    public static WriteHandlerPtr polepos_road_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            COMBINE_WORD_MEM(polepos_road_memory, offset, data);
        }
    };

    public static ReadHandlerPtr polepos_z80_road_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_road_r.handler(offset << 1) & 0xff;
        }
    };

    public static WriteHandlerPtr polepos_z80_road_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            polepos_road_w.handler(offset << 1, data | 0xff000000);
        }
    };

    public static WriteHandlerPtr polepos_road_vscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            road_vscroll = (char) data;
        }
    };

    /**
     * *************************************************************************
     *
     * View memory
     *
     **************************************************************************
     */
    public static ReadHandlerPtr polepos_view_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_view_memory.READ_WORD(offset);
        }
    };

    public static WriteHandlerPtr polepos_view_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int oldword = polepos_view_memory.READ_WORD(offset);
            int newword = COMBINE_WORD(oldword, data);
            if (oldword != newword) {
                polepos_view_memory.WRITE_WORD(offset, newword);
                if (offset < 0x800) {
                    u8_view_dirty[offset / 2] = 1;
                }
            }
        }
    };

    public static ReadHandlerPtr polepos_z80_view_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_view_r.handler(offset << 1) & 0xff;
        }
    };

    public static WriteHandlerPtr polepos_z80_view_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            polepos_view_w.handler(offset << 1, data | 0xff000000);
        }
    };

    public static WriteHandlerPtr polepos_view_hscroll_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            view_hscroll = (char) data;
        }
    };

    /**
     * *************************************************************************
     *
     * Alpha memory
     *
     **************************************************************************
     */
    public static ReadHandlerPtr polepos_alpha_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_alpha_memory.READ_WORD(offset);
        }
    };

    public static WriteHandlerPtr polepos_alpha_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int oldword = polepos_alpha_memory.READ_WORD(offset);
            int newword = COMBINE_WORD(oldword, data);
            if (oldword != newword) {
                polepos_alpha_memory.WRITE_WORD(offset, newword);
            }
        }
    };

    public static ReadHandlerPtr polepos_z80_alpha_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return polepos_alpha_r.handler(offset << 1) & 0xff;
        }
    };

    public static WriteHandlerPtr polepos_z80_alpha_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            polepos_alpha_w.handler(offset << 1, data | 0xff000000);
        }
    };

    /**
     * *************************************************************************
     *
     * Internal draw routines
     *
     **************************************************************************
     */
    static void draw_view(osd_bitmap bitmap) {
        rectangle clip = new rectangle(Machine.visible_area);
        int y, offs;
        int x[] = new int[1];
        /* look for dirty tiles */
        for (x[0] = offs = 0; x[0] < 64; x[0]++) {
            for (y = 0; y < 16; y++, offs++) {
                if (u8_view_dirty[offs] != 0) {
                    int word = polepos_view_memory.READ_WORD(offs * 2);
                    int code = (word & 0xff) | ((word >> 6) & 0x100);
                    int color = (word >> 8) & 0x3f;

                    drawgfx(view_bitmap, Machine.gfx[1], code, color,
                            0, 0, 8 * x[0], 8 * y, null, TRANSPARENCY_NONE, 0);
                    u8_view_dirty[offs] = 0;
                }
            }
        }

        /* copy the bitmap */
        x[0] = -view_hscroll;
        clip.max_y = 127;
        copyscrollbitmap(bitmap, view_bitmap, 1, x, 0, null, clip, TRANSPARENCY_NONE, 0);
    }

    	
	static void draw_road(osd_bitmap bitmap)
	{
		int dx = 1, dy = (bitmap.line[1].offset - bitmap.line[0].offset) * 8 / bitmap.depth;
		int sx = 0, sy = 128, temp;
	
		/* adjust our parameters for the current orientation */
		if (Machine.orientation != 0)
		{
			if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0)
			{
				temp = sx; sx = sy; sy = temp;
				temp = dx; dx = dy; dy = temp;
			}
			if ((Machine.orientation & ORIENTATION_FLIP_X) != 0)
			{
				sx = bitmap.width - 1 - sx;
				if ((Machine.orientation & ORIENTATION_SWAP_XY)==0) dx = -dx;
				else dy = -dy;
			}
			if ((Machine.orientation & ORIENTATION_FLIP_Y) != 0)
			{
				sy = bitmap.height - 1 - sy;
				if ((Machine.orientation & ORIENTATION_SWAP_XY)!=0) dx = -dx;
				else dy = -dy;
			}
		}
	
		/* 8-bit case */
		if (bitmap.depth == 8){
                    //System.out.println("draw_road_core_8");
			draw_road_core_8(bitmap, sx, sy, dx, dy);
                } else {
                    System.out.println("draw_road_core_16");
/*TODO*///			draw_road_core_16(bitmap, sx, sy, dx, dy);
                }
	}
	
	static void draw_sprites(osd_bitmap bitmap)
	{
		UShortArray posmem = new UShortArray(polepos_sprite_memory, 0x700);
		UShortArray sizmem = new UShortArray(polepos_sprite_memory, 0xf00);
		int i;
	
		for (i = 0; i < 64; i++, posmem.offset+=2, sizmem.offset+=2 )
		{
			GfxElement gfx = Machine.gfx[(sizmem.read(0) & 0x8000)!=0 ? 3 : 2];
			int vpos = (~posmem.read(0) & 0x1ff) + 4;
			int hpos = (posmem.read(1) & 0x3ff) - 0x40;
			int vsize = ((sizmem.read(0) >> 8) & 0x3f) + 1;
			int hsize = ((sizmem.read(1) >> 8) & 0x3f) + 1;
			int code = sizmem.read(0) & 0x7f;
			int hflip = sizmem.read(0) & 0x80;
			int color = sizmem.read(1) & 0x3f;
	
			if (vpos >= 128) color |= 0x40;
			drawgfxzoom(bitmap, gfx,
					 code, color, hflip, 0, hpos, vpos,
					 Machine.visible_area, TRANSPARENCY_COLOR, 0, hsize << 11, vsize << 11);
		}
	}

    static void draw_alpha(osd_bitmap bitmap) {
        int x, y, offs, in;

        for (y = offs = 0; y < 32; y++) {
            for (x = 0; x < 32; x++, offs++) {
                int word = polepos_alpha_memory.READ_WORD(offs * 2);
                int code = (word & 0xff) | ((word >> 6) & 0x100);
                int color = (word >> 8) & 0x3f;
                /* 6 bits color */

                if (y >= 16) {
                    color |= 0x40;
                }
                drawgfx(bitmap, Machine.gfx[0],
                        code, color, 0, 0, 8 * x, 8 * y,
                        Machine.visible_area, TRANSPARENCY_COLOR, 0);
            }
        }

        /* Now draw the shift if selected on the fake dipswitch */
        in = readinputport(0);

        if ((in & 8) != 0) {
            if ((in & 2) == 0) {
                /* L */
                drawgfx(bitmap, Machine.gfx[0],
                        0x15, 0, 0, 0, 30 * 8 - 1, 29 * 8,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
                /* O */
                drawgfx(bitmap, Machine.gfx[0],
                        0x18, 0, 0, 0, 31 * 8 - 1, 29 * 8,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            } else {
                /* H */
                drawgfx(bitmap, Machine.gfx[0],
                        0x11, 0, 0, 0, 30 * 8 - 1, 29 * 8,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
                /* I */
                drawgfx(bitmap, Machine.gfx[0],
                        0x12, 0, 0, 0, 31 * 8 - 1, 29 * 8,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);
            }
        }
    }

    /**
     * *************************************************************************
     *
     * Master refresh routine
     *
     **************************************************************************
     */
    public static VhUpdatePtr polepos_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            draw_view(bitmap);
            draw_road(bitmap);
            draw_sprites(bitmap);
            draw_alpha(bitmap);
        }
    };

    /*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///	  Road drawing generators
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	#define ROAD_CORE_INCLUDE
/*TODO*///	
/*TODO*///	#define NAME draw_road_core_8
/*TODO*///	#define TYPE UINT8
/*TODO*///	#undef TYPE
/*TODO*///	#undef NAME
/*TODO*///	
/*TODO*///	#define NAME draw_road_core_16
/*TODO*///	#define TYPE UINT16
/*TODO*///	#undef TYPE
/*TODO*///	#undef NAME
/*TODO*///	
/*TODO*///	#else
/*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///	  Road drawing routine
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	static void NAME(struct osd_bitmap *bitmap, int sx, int sy, int dx, int dy)
/*TODO*///	{
/*TODO*///		TYPE *base = &((TYPE *)bitmap.line[sy])[sx];
/*TODO*///		int x, y, i;
/*TODO*///	
/*TODO*///		/* loop over the lower half of the screen */
/*TODO*///		for (y = 128; y < 256; y++, base += dy)
/*TODO*///		{
/*TODO*///			int xoffs, yoffs, roadpal;
/*TODO*///			UINT16 *colortable;
/*TODO*///			TYPE *dest;
/*TODO*///	
/*TODO*///			/* first add the vertical position modifier and the vertical scroll */
/*TODO*///			yoffs = ((polepos_vertical_position_modifier[y] + road_vscroll) >> 2) & 0x3fe;
/*TODO*///	
/*TODO*///			/* then use that as a lookup into the road memory */
/*TODO*///			roadpal = READ_WORD(&polepos_road_memory[yoffs]) & 15;
/*TODO*///	
/*TODO*///			/* this becomes the palette base for the scanline */
/*TODO*///			colortable = &Machine.remapped_colortable[0x1000 + (roadpal << 6)];
/*TODO*///	
/*TODO*///			/* now fetch the horizontal scroll offset for this scanline */
/*TODO*///			xoffs = READ_WORD(&polepos_road_memory[0x700 + (y & 0x7f) * 2]) & 0x3ff;
/*TODO*///	
/*TODO*///			/* the road is drawn in 8-pixel chunks, so round downward and adjust the base */
/*TODO*///			/* note that we assume there is at least 8 pixels of slop on the left/right */
/*TODO*///			dest = base - (xoffs & 7) * dx;
/*TODO*///			xoffs &= ~7;
/*TODO*///	
/*TODO*///			/* loop over 8-pixel chunks */
/*TODO*///			for (x = 0; x < 256 / 8 + 1; x++, xoffs += 8)
/*TODO*///			{
/*TODO*///				/* if the 0x200 bit of the xoffset is set, a special pin on the custom */
/*TODO*///				/* chip is set and the /CE and /OE for the road chips is disabled */
/*TODO*///				if ((xoffs & 0x200) != 0)
/*TODO*///				{
/*TODO*///					/* in this case, it looks like we just fill with 0 */
/*TODO*///					for (i = 0; i < 8; i++, dest += dx)
/*TODO*///						*dest = colortable[0];
/*TODO*///				}
/*TODO*///	
/*TODO*///				/* otherwise, we clock in the bits and compute the road value */
/*TODO*///				else
/*TODO*///				{
/*TODO*///					/* the road ROM offset comes from the current scanline and the X offset */
/*TODO*///					int romoffs = ((y & 0x07f) << 6) + ((xoffs & 0x1ff) >> 3);
/*TODO*///	
/*TODO*///					/* fetch the current data from the road ROMs */
/*TODO*///					int control = road_control[romoffs];
/*TODO*///					int bits1 = road_bits1[romoffs];
/*TODO*///					int bits2 = road_bits2[(romoffs & 0xfff) | ((romoffs >> 1) & 0x800)];
/*TODO*///	
/*TODO*///					/* extract the road value and the carry-in bit */
/*TODO*///					int roadval = control & 0x3f;
/*TODO*///					int carin = control >> 7;
/*TODO*///	
/*TODO*///					/* draw this 8-pixel chunk */
/*TODO*///					for (i = 0; i < 8; i++, dest += dx, bits1 <<= 1, bits2 <<= 1)
/*TODO*///					{
/*TODO*///						int bits = ((bits1 >> 7) & 1) + ((bits2 >> 6) & 2);
/*TODO*///						if (!carin && bits) bits++;
/*TODO*///						*dest = colortable[roadval & 0x3f];
/*TODO*///						roadval += bits;
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	#endif
    
    	
	/***************************************************************************
	
	  Road drawing routine
	
	***************************************************************************/
	
	static void draw_road_core_8(osd_bitmap bitmap, int sx, int sy, int dx, int dy)
	{
		UBytePtr base = new UBytePtr(bitmap.line[sy], sx);
		int x, y, i;
	
		/* loop over the lower half of the screen */
		for (y = 128; y < 256; y++, base.inc(dy) )
		{
			int xoffs, yoffs, roadpal;
			UShortArray colortable;
			UBytePtr dest;
	
			/* first add the vertical position modifier and the vertical scroll */
			yoffs = ((polepos_vertical_position_modifier[y] + road_vscroll) >> 2) & 0x3fe;
	
			/* then use that as a lookup into the road memory */
			roadpal = polepos_road_memory.READ_WORD(yoffs) & 15;
	
			/* this becomes the palette base for the scanline */
			colortable = new UShortArray(Machine.remapped_colortable, 0x1000 + (roadpal << 6));
	
			/* now fetch the horizontal scroll offset for this scanline */
			xoffs = polepos_road_memory.READ_WORD(0x700 + (y & 0x7f) * 2) & 0x3ff;
	
			/* the road is drawn in 8-pixel chunks, so round downward and adjust the base */
			/* note that we assume there is at least 8 pixels of slop on the left/right */
			dest = new UBytePtr(base, - (xoffs & 7) * dx);
			xoffs &= ~7;
	
			/* loop over 8-pixel chunks */
			for (x = 0; x < 256 / 8 + 1; x++, xoffs += 8)
			{
				/* if the 0x200 bit of the xoffset is set, a special pin on the custom */
				/* chip is set and the /CE and /OE for the road chips is disabled */
				if ((xoffs & 0x200) != 0)
				{
					/* in this case, it looks like we just fill with 0 */
					for (i = 0; i < 8; i++, dest.inc(dx) )
						dest.write(0, colortable.read(0));
				}
	
				/* otherwise, we clock in the bits and compute the road value */
				else
				{
					/* the road ROM offset comes from the current scanline and the X offset */
					int romoffs = ((y & 0x07f) << 6) + ((xoffs & 0x1ff) >> 3);
	
					/* fetch the current data from the road ROMs */
					int control = road_control.read(romoffs);
					int bits1 = road_bits1.read(romoffs);
					int bits2 = road_bits2.read((romoffs & 0xfff) | ((romoffs >> 1) & 0x800));
	
					/* extract the road value and the carry-in bit */
					int roadval = control & 0x3f;
					int carin = control >> 7;
	
					/* draw this 8-pixel chunk */
					for (i = 0; i < 8; i++, dest.inc(dx), bits1 <<= 1, bits2 <<= 1)
					{
						int bits = ((bits1 >> 7) & 1) + ((bits2 >> 6) & 2);
						if (carin==0 && bits!=0) bits++;
						dest.write(0, colortable.read(roadval & 0x3f));
						roadval += bits;
					}
				}
			}
		}
	}

}
