/*************************************************************************

	 Turbo - Sega - 1981

	 Video Hardware

*************************************************************************/

//#ifndef DRAW_CORE_INCLUDE

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import gr.codebb.arcadeflex.common.SubArrays.IntSubArray;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.mame.common.memory_region_length;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
        
import static gr.codebb.arcadeflex.WIP.v037b7.machine.turbo.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.readinputport;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;

public class turbo
{
	
	/* constants */
	public static int VIEW_WIDTH    = (32*8);
	public static int VIEW_HEIGHT   = (28*8);
	
	/* external definitions */
	public static UBytePtr turbo_sprite_position = new UBytePtr();
	public static int turbo_collision;
	
	/* internal data */
	static UBytePtr sprite_gfxdata, sprite_priority;
	static UBytePtr road_gfxdata, road_palette, road_enable_collide;
	static UBytePtr back_gfxdata, back_palette;
	static UBytePtr overall_priority, collision_map;
	
	/* sprite tracking */
	public static class sprite_params_data
	{
		IntSubArray base;
		int offset, rowbytes;
		int yscale, miny, maxy;
		int xscale, xoffs;
	};
        
	static sprite_params_data[] sprite_params = new sprite_params_data[16];
        
	static IntSubArray sprite_expanded_data;
	
	/* orientation */
	static rectangle game_clip = new rectangle( 0, VIEW_WIDTH - 1, 64, 64 + VIEW_HEIGHT - 1 );
	static rectangle adjusted_clip = new rectangle();
	static int startx, starty, deltax, deltay;
	
	/* misc other stuff */
	static IntSubArray back_expanded_data;
	static IntSubArray road_expanded_palette;
	static int drew_frame;
	
	/***************************************************************************
	
	  Convert the color PROMs into a more useable format.
	
	***************************************************************************/
	
	public static VhConvertColorPromPtr turbo_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int i;
	
		for (i = 0; i < 512; i++, color_prom.inc())
		{
			int bit0, bit1, bit2;
	
			/* bits 4,5,6 of the index are inverted before being used as addresses */
			/* to save ourselves lots of trouble, we will undo the inversion when */
			/* generating the palette */
			int adjusted_index = i ^ 0x70;
	
			/* red component */
			bit0 = (color_prom.read() >> 0) & 1;
			bit1 = (color_prom.read() >> 1) & 1;
			bit2 = (color_prom.read() >> 2) & 1;
			palette[adjusted_index * 3 + 0] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
	
			/* green component */
			bit0 = (color_prom.read() >> 3) & 1;
			bit1 = (color_prom.read() >> 4) & 1;
			bit2 = (color_prom.read() >> 5) & 1;
			palette[adjusted_index * 3 + 1] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
	
			/* blue component */
			bit0 = 0;
			bit1 = (color_prom.read() >> 6) & 1;
			bit2 = (color_prom.read() >> 7) & 1;
			palette[adjusted_index * 3 + 2] = (char) (0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2);
		}
	
		/* LED segments colors: black and red */
		palette[512 * 3 + 0] = 0x00;
		palette[512 * 3 + 1] = 0x00;
		palette[512 * 3 + 2] = 0x00;
		palette[513 * 3 + 0] = 0xff;
		palette[513 * 3 + 1] = 0x00;
		palette[513 * 3 + 2] = 0x00;
		/* Tachometer colors: Led colors + yellow and green */
		palette[514 * 3 + 0] = 0x00;
		palette[514 * 3 + 1] = 0x00;
		palette[514 * 3 + 2] = 0x00;
		palette[515 * 3 + 0] = 0xff;
		palette[515 * 3 + 1] = 0xff;
		palette[515 * 3 + 2] = 0x00;
		palette[516 * 3 + 0] = 0x00;
		palette[516 * 3 + 1] = 0x00;
		palette[516 * 3 + 2] = 0x00;
		palette[517 * 3 + 0] = 0x00;
		palette[517 * 3 + 1] = 0xff;
		palette[517 * 3 + 2] = 0x00;
	} };
	
	
	/***************************************************************************
	
	  Video startup/shutdown
	
	***************************************************************************/
	
	public static VhStartPtr turbo_vh_start = new VhStartPtr() { public int handler() 
	{
		int i, j, sprite_length, sprite_bank_size, back_length;
		IntSubArray sprite_expand=new IntSubArray(16);
		IntSubArray dst;
		IntSubArray bdst;
		UBytePtr src;
	
		/* allocate the expanded sprite data */
		sprite_length = memory_region_length(REGION_GFX1);
		sprite_bank_size = sprite_length / 8;
		sprite_expanded_data = new IntSubArray(sprite_length * 2);
		if (sprite_expanded_data==null)
			return 1;
	
		/* allocate the expanded background data */
		back_length = memory_region_length(REGION_GFX3);
		back_expanded_data = new IntSubArray(back_length);
		if (back_expanded_data==null)
		{
			sprite_expanded_data = null;
			return 1;
		}
	
		/* allocate the expanded road palette */
		road_expanded_palette = new IntSubArray(0x40);
		if (road_expanded_palette == null)
		{
			back_expanded_data = null;
			sprite_expanded_data = null;
			return 1;
		}
	
		/* determine ROM/PROM addresses */
		sprite_gfxdata = new UBytePtr(memory_region(REGION_GFX1));
		sprite_priority = new UBytePtr(memory_region(REGION_PROMS), 0x0200);
	
		road_gfxdata = new UBytePtr(memory_region(REGION_GFX2));
		road_palette = new UBytePtr(memory_region(REGION_PROMS), 0x0b00);
		road_enable_collide = new UBytePtr(memory_region(REGION_PROMS), 0x0b40);
	
		back_gfxdata = new UBytePtr(memory_region(REGION_GFX3));
		back_palette = new UBytePtr(memory_region(REGION_PROMS), 0x0a00);
	
		overall_priority = new UBytePtr(memory_region(REGION_PROMS), 0x0600);
		collision_map = new UBytePtr(memory_region(REGION_PROMS), 0x0b60);
	
		/* compute the sprite expansion array */
		for (i = 0; i < 16; i++)
		{
			int value = 0;
			if ((i & 1) != 0) value |= 0x00000001;
			if ((i & 2) != 0) value |= 0x00000100;
			if ((i & 4) != 0) value |= 0x00010000;
			if ((i & 8) != 0) value |= 0x01000000;
	
			/* special value for the end-of-row */
			if ((i & 0x0c) == 0x04) value = 0x12345678;
	
			sprite_expand.write(i, value);
		}
	
		/* expand the sprite ROMs */
		src = new UBytePtr(sprite_gfxdata);
		dst = new IntSubArray(sprite_expanded_data);
                int _dst=0;
		for (i = 0; i < 8; i++)
		{
			/* expand this bank */
			for (j = 0; j < sprite_bank_size; j++)
			{
				dst.write(_dst++, sprite_expand.read(src.read() >> 4));
                                dst.write(_dst++, sprite_expand.read(src.readinc() & 15));
			}
	
			/* shift for the next bank */
			for (j = 0; j < 16; j++)
				if (sprite_expand.read(j) != 0x12345678) sprite_expand.write(j, sprite_expand.read(j) << 1);
		}
	
		/* expand the background ROMs */
		src = new UBytePtr(back_gfxdata,0);
		bdst = new IntSubArray(back_expanded_data);
		for (i = 0; i < back_length / 2; i++, src.inc())
		{
			int bits1 = src.read(0);
			int bits2 = src.read(back_length / 2);
			int newbits = 0;
	
			for (j = 0; j < 8; j++)
			{
				newbits |= ((bits1 >> (j ^ 7)) & 1) << (j * 2);
				newbits |= ((bits2 >> (j ^ 7)) & 1) << (j * 2 + 1);
			}
			bdst.write(0, newbits);
                        bdst.inc(1);
		}
	
		/* expand the road palette */
		src = road_palette;
		bdst = road_expanded_palette;
		for (i = 0; i < 0x20; i++, src.inc()){
			bdst.write(0, src.read(0) | (src.read(0x20) << 8));
                        bdst.inc(1);
                }
	
		/* set the default drawing parameters */
		startx = game_clip.min_x;
		starty = game_clip.min_y;
		deltax = deltay = 1;
		adjusted_clip = new rectangle(game_clip);
	
		/* adjust our parameters for the specified orientation */
		if (Machine.orientation != 0)
		{
			int temp;
	
			if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0)
			{
				temp = startx; startx = starty; starty = temp;
				temp = adjusted_clip.min_x; adjusted_clip.min_x = adjusted_clip.min_y; adjusted_clip.min_y = temp;
				temp = adjusted_clip.max_x; adjusted_clip.max_x = adjusted_clip.max_y; adjusted_clip.max_y = temp;
			}
			if ((Machine.orientation & ORIENTATION_FLIP_X) != 0)
			{
				startx = adjusted_clip.max_x;
				if ((Machine.orientation & ORIENTATION_SWAP_XY)==0) deltax = -deltax;
				else deltay = -deltay;
			}
			if ((Machine.orientation & ORIENTATION_FLIP_Y) != 0)
			{
				starty = adjusted_clip.max_y;
				if ((Machine.orientation & ORIENTATION_SWAP_XY)==0) deltay = -deltay;
				else deltax = -deltax;
			}
		}
	
		/* other stuff */
		drew_frame = 0;
	
		/* return success */
		return 0;
	} };
	
	
	public static VhStopPtr turbo_vh_stop = new VhStopPtr() { public void handler() 
	{
		sprite_expanded_data = null;
		back_expanded_data = null;
		road_expanded_palette = null;
	} };
	
	
	/***************************************************************************
	
	  Sprite information
	
	***************************************************************************/
	
	static void update_sprite_info()
	{
		//sprite_params_data[] data = sprite_params;
                int _data=0;
                
		int i;
	
		/* first loop over all sprites and update those whose scanlines intersect ours */
		for (i = 0; i < 16; i++, _data++)
		{
			UBytePtr sprite_base = new UBytePtr(spriteram, 16 * i);
	
			/* snarf all the data */
                        if (sprite_params[_data] == null)
                            sprite_params[_data] = new sprite_params_data();
                        
			sprite_params[_data].base = new IntSubArray(sprite_expanded_data, (i & 7) * 0x8000);
			sprite_params[_data].offset = (sprite_base.read(6) + 256 * sprite_base.read(7)) & 0x7fff;
			sprite_params[_data].rowbytes = (sprite_base.read(4) + 256 * sprite_base.read(5));
			sprite_params[_data].miny = sprite_base.read(0);
			sprite_params[_data].maxy = sprite_base.read(1);
			sprite_params[_data].xscale = ((5 * 256 - 4 * sprite_base.read(2)) << 16) / (5 * 256);
			sprite_params[_data].yscale = (4 << 16) / (sprite_base.read(3) + 4);
			sprite_params[_data].xoffs = -1;
		}
	
		/* now find the X positions */
		for (i = 0; i < 0x200; i++)
		{
			int value = turbo_sprite_position.read(i);
			if (value != 0)
			{
				int base = (i & 0x100) >> 5;
				int which;
				for (which = 0; which < 8; which++)
					if ((value & (1 << which)) != 0)
						sprite_params[base + which].xoffs = i & 0xff;
			}
		}
	}
	
	
	/***************************************************************************
	
	  Internal draw routines
	
	***************************************************************************/
	
	static IntSubArray draw_one_sprite(sprite_params_data data, IntSubArray dest, int xclip, int scanline)
	{
            //System.out.println("draw_one_sprite");
		int xstep = data.xscale;
		int xoffs = data.xoffs;
		int xcurr, offset;
		IntSubArray src;
	
		/* xoffs of -1 means don't draw */
		if (xoffs == -1) return dest;
	
		/* clip to the road */
		xcurr = 0;
		if (xoffs < xclip)
		{
			/* the pixel clock starts on xoffs regardless of clipping; take this into account */
			xcurr = ((xclip - xoffs) * xstep) & 0xffff;
			xoffs = xclip;
		}
	
		/* compute the current data offset */
		scanline = ((scanline - data.miny) * data.yscale) >> 16;
                //data.offset=0;
		offset = data.offset + (scanline + 1) * data.rowbytes;
                //int _offsOLD=data.base.offset;
	
		/* determine the bitmap location */
		src = new IntSubArray(data.base, offset & 0x7fff);
                //src.offset = (data.base.offset + offset) & 0x7fff;
                //src.offset -= _offsOLD;
	
		/* loop over columns */
		while (xoffs < VIEW_WIDTH)
		{
			int srcval = src.read(xcurr >> 16);
	
			/* stop on the end-of-row signal */
			if (srcval == 0x12345678) break;
			//dest[xoffs] |= srcval;
                        dest.write(xoffs, dest.read(xoffs) | srcval);
                        xoffs++;
			xcurr += xstep;
		}
                //System.out.println("offsetXXXX: "+dest.offset);
                //dest.offset=0;
                
                return dest;
	}
	
	
	static IntSubArray draw_road_sprites(IntSubArray dest, int scanline)
	{
		int param_list[] =
		{
			0, 8,
			1, 9,
			2, 10
		};
		int i;
	
		/* loop over the road sprites */
		for (i = 0; i < 6; i++)
		{
			sprite_params_data data = sprite_params[ param_list[i]];
	
			/* if the sprite intersects this scanline, draw it */
			if (scanline >= data.miny && scanline < data.maxy)
				dest = draw_one_sprite(data, dest, 0, scanline);
                        
                        sprite_params[ param_list[i]] = data;
		}
                
                return dest;
	}
	
	
	static IntSubArray draw_offroad_sprites(IntSubArray dest, int road_column, int scanline)
	{
		int param_list[] =
		{
			3, 11,
			4, 12,
			5, 13,
			6, 14,
			7, 15
		};
		int i;
	
		/* loop over the offroad sprites */
		for (i = 0; i < 10; i++)
		{
			sprite_params_data data = sprite_params[ param_list[i]];
	
			/* if the sprite intersects this scanline, draw it */
			if (scanline >= data.miny && scanline < data.maxy)
				dest = draw_one_sprite(data, dest, road_column, scanline);
                        
                        sprite_params[ param_list[i]] = data;
		}
                
                return dest;
	}
	
	
	static void draw_scores(osd_bitmap bitmap)
	{
		rectangle clip = new rectangle();
		int offs, x, y;
	
		/* current score */
		offs = 31;
		for (y = 0; y < 5; y++, offs--)
			drawgfx(bitmap, Machine.gfx[0],
					turbo_segment_data[offs],
					0,
					0, 0,
					14*8, (2 + y) * 8,
					new rectangle(Machine.visible_area), TRANSPARENCY_NONE, 0);
	
		/* high scores */
		for (x = 0; x < 5; x++)
		{
			offs = 6 + x * 5;
			for (y = 0; y < 5; y++, offs--)
				drawgfx(bitmap, Machine.gfx[0],
						turbo_segment_data[offs],
						0,
						0, 0,
						(20 + 2 * x) * 8, (2 + y) * 8,
						new rectangle(Machine.visible_area), TRANSPARENCY_NONE, 0);
		}
	
		/* tachometer */
		clip = new rectangle(Machine.visible_area);
		clip.min_x = 5*8;
		clip.max_x = clip.min_x + 1;
		for (y = 0; y < 22; y++)
		{
			int led_color[] = { 2, 2, 2, 2, 1, 1, 0, 0, 0, 0, 0 };
			int code = ((y / 2) <= turbo_speed) ? 0 : 1;
	
			drawgfx(bitmap, Machine.gfx[1],
					code,
					led_color[y / 2],
					0,0,
					5*8, y*2+8,
					new rectangle(clip), TRANSPARENCY_NONE, 0);
			if (y % 3 == 2)
				clip.max_x++;
		}
	
		/* shifter status */
		if ((readinputport(0) & 0x04) != 0)
		{
			drawgfx(bitmap, Machine.gfx[2], 'H', 0, 0,0, 2*8,3*8, new rectangle(Machine.visible_area), TRANSPARENCY_NONE, 0);
			drawgfx(bitmap, Machine.gfx[2], 'I', 0, 0,0, 2*8,4*8, new rectangle(Machine.visible_area), TRANSPARENCY_NONE, 0);
		}
		else
		{
			drawgfx(bitmap, Machine.gfx[2], 'L', 0, 0,0, 2*8,3*8, new rectangle(Machine.visible_area), TRANSPARENCY_NONE, 0);
			drawgfx(bitmap, Machine.gfx[2], 'O', 0, 0,0, 2*8,4*8, new rectangle(Machine.visible_area), TRANSPARENCY_NONE, 0);
		}
	}
	
	
	
	/***************************************************************************
	
	  Master refresh routine
	
	***************************************************************************/
	
	public static VhEofCallbackPtr turbo_vh_eof = new VhEofCallbackPtr() {
            @Override
            public void handler() {
                /* only do collision checking if we didn't draw */
		if (drew_frame==0)
		{
			update_sprite_info();
                        System.out.println("draw_minimal");
/*TODO*///			draw_minimal(Machine.scrbitmap);
		}
		drew_frame = 0;
            }
        };
	
	public static VhUpdatePtr turbo_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		/* update the sprite data */
		update_sprite_info();
	
		/* perform the actual drawing */
		if (bitmap.depth == 8){
                    //System.out.println("draw_everything_core_8");
			draw_everything_core_8(bitmap);
                } else {
                    System.out.println("draw_everything_core_16");
/*TODO*///			draw_everything_core_16(bitmap);
                }
	
		/* draw the LEDs for the scores */
		draw_scores(bitmap);
	
		/* indicate that we drew this frame, so that the eof callback doesn't bother doing anything */
		drew_frame = 1;
	} };
	
	
	/***************************************************************************
	
	  Road drawing generators
	
	***************************************************************************/
        
        static void draw_everything_core_8(osd_bitmap bitmap)
	{
                int FULL_DRAW = 1;
		UBytePtr base = new UBytePtr(bitmap.line[starty], startx);
		IntSubArray sprite_buffer=new IntSubArray((VIEW_WIDTH + 256) * 2);
                
                int _origin = base.offset;
	
		UBytePtr overall_priority_base = new UBytePtr(overall_priority, (turbo_fbpla & 8) << 6);
		UBytePtr sprite_priority_base = new UBytePtr(sprite_priority, (turbo_fbpla & 7) << 7);
		UBytePtr road_gfxdata_base = new UBytePtr(road_gfxdata, (turbo_opc << 5) & 0x7e0);
		IntSubArray  road_palette_base = new IntSubArray(road_expanded_palette, (turbo_fbcol & 1) << 4);
	
		int dx = deltax, dy = deltay, rowsize = (bitmap.line[1].offset - bitmap.line[0].offset) * 8 / bitmap.depth;
		IntSubArray  colortable;
		int x, y, i;
	
		/* expand the appropriate delta */
		if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0)
			dx *= rowsize;
		else
			dy *= rowsize;
	
		/* determine the color offset */
		colortable = new IntSubArray(Machine.pens, (turbo_fbcol & 6) << 6);
                
                int _cont = 0;
	
		/* loop over rows */
		for (y = 4; y < VIEW_HEIGHT - 4; y++, base.inc(dy))
		{
			int sel, coch, babit, slipar_acciar, area, area1, area2, area3, area4, area5, road = 0;
			IntSubArray sprite_data = new IntSubArray(sprite_buffer);
                        //System.out.println("--#"+sprite_buffer.offset);
                        //sprite_data.offset=0;
                        //sprite_buffer.offset=0;
                        
			UBytePtr dest = new UBytePtr(base);
                        //dest.offset = _origin + _cont;
                        //_cont +=dy;
	
			/* compute the Y sum between opa and the current scanline (p. 141) */
			int va = (y + turbo_opa) & 0xff;
	
			/* the upper bit of OPC inverts the road */
			if ((turbo_opc & 0x80)==0) va ^= 0xff;
	
			/* clear the sprite buffer and draw the road sprites */
                        //memset(sprite_buffer, 0, VIEW_WIDTH);
                        for (int _k=0 ; _k<VIEW_WIDTH ; _k++ )
                            sprite_buffer.buffer[_k]=0;
                        
                        draw_road_sprites(sprite_buffer, y);
                        
			/* loop over 8-pixel chunks */
			dest.inc( dx * 8 );
                        
			sprite_data.inc( 8 );
			for (x = 8; x < VIEW_WIDTH; x += 8)
			{
				int area5_buffer = road_gfxdata_base.read(0x4000 + (x >> 3));
				int back_data = videoram.read((y / 8) * 32 + (x / 8) - 33);
				int backbits_buffer = back_expanded_data.read((back_data << 3) | (y & 7));
	
				/* loop over columns */
				for (i = 0; i < 8; i++, dest.inc(dx))
				{
                                    //System.out.println("offset2: "+dest.offset);
					int sprite = sprite_data.read();
                                        sprite_data.inc(1);
	
					/* compute the X sum between opb and the current column; only the carry matters (p. 141) */
					int carry = (x + i + turbo_opb) >> 8;
	
					/* the carry selects which inputs to use (p. 141) */
					if (carry != 0)
					{
						sel	 = turbo_ipb;
						coch = turbo_ipc >> 4;
					}
					else
					{
						sel	 = turbo_ipa;
						coch = turbo_ipc & 15;
					}
	
					/* at this point we also compute area5 (p. 141) */
					area5 = (area5_buffer >> 3) & 0x10;
					area5_buffer <<= 1;
	
					/* now look up the rest of the road bits (p. 142) */
					area1 = road_gfxdata.read(0x0000 | ((sel & 15) << 8) | va);
					area1 = ((area1 + x + i) >> 8) & 0x01;
					area2 = road_gfxdata.read(0x1000 | ((sel & 15) << 8) | va);
					area2 = ((area2 + x + i) >> 7) & 0x02;
					area3 = road_gfxdata.read(0x2000 | ((sel >> 4) << 8) | va);
					area3 = ((area3 + x + i) >> 6) & 0x04;
					area4 = road_gfxdata.read(0x3000 | ((sel >> 4) << 8) | va);
					area4 = ((area4 + x + i) >> 5) & 0x08;
	
					/* compute the final area value and look it up in IC18/PR1115 (p. 144) */
					area = area5 | area4 | area3 | area2 | area1;
					babit = road_enable_collide.read(area) & 0x07;
	
					/* note: SLIPAR is 0 on the road surface only */
					/*		 ACCIAR is 0 on the road surface and the striped edges only */
					slipar_acciar = road_enable_collide.read(area) & 0x30;
					if (road==0 && (slipar_acciar & 0x20)!=0)
					{
						road = 1;
						draw_offroad_sprites(sprite_buffer, x + i + 2, y);
					}
	
					/* perform collision detection here */
					turbo_collision |= collision_map.read(((sprite >> 24) & 7) | (slipar_acciar >> 1));
	
					/* we only need to continue if we're actually drawing */
					if (FULL_DRAW != 0)
					{
						int bacol, red, grn, blu, priority, backbits, mx;
	
						/* also use the coch value to look up color info in IC13/PR1114 and IC21/PR1117 (p. 144) */
						bacol = road_palette_base.read(coch & 15);
	
						/* at this point, do the character lookup */
						backbits = backbits_buffer & 3;
						backbits_buffer >>= 2;
						backbits = back_palette.read(backbits | (back_data & 0xfc));
	
						/* look up the sprite priority in IC11/PR1122 */
						priority = sprite_priority_base.read(sprite >> 25);
	
						/* use that to look up the overall priority in IC12/PR1123 */
						mx = overall_priority_base.read((priority & 7) | ((sprite >> 21) & 8) | ((back_data >> 3) & 0x10) | ((backbits << 2) & 0x20) | (babit << 6));
	
						/* the input colors consist of a mix of sprite, road and 1's & 0's */
						red = 0x040000 | ((bacol & 0x001f) << 13) | ((backbits & 1) << 12) | ((sprite <<  4) & 0x0ff0);
						grn = 0x080000 | ((bacol & 0x03e0) <<  9) | ((backbits & 2) << 12) | ((sprite >>  3) & 0x1fe0);
						blu = 0x100000 | ((bacol & 0x7c00) <<  5) | ((backbits & 4) << 12) | ((sprite >> 10) & 0x3fc0);
	
						/* we then go through a muxer; normally these values are inverted, but */
						/* we've already taken care of that when we generated the palette */
						red = (red >> mx) & 0x10;
						grn = (grn >> mx) & 0x20;
						blu = (blu >> mx) & 0x40;
						dest.write(0, colortable.read(mx | red | grn | blu));
					}
				}
			}
		}
	}
	
/*TODO*///	#define DRAW_CORE_INCLUDE
/*TODO*///	
/*TODO*///	#define FULL_DRAW	1
/*TODO*///	
/*TODO*///	#define DRAW_FUNC	draw_everything_core_8
/*TODO*///	#define TYPE		UINT8
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	#define DRAW_FUNC	draw_everything_core_16
/*TODO*///	#define TYPE		UINT16
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	#undef FULL_DRAW
/*TODO*///	#define FULL_DRAW	0
/*TODO*///	
/*TODO*///	#define DRAW_FUNC	draw_minimal
/*TODO*///	#define TYPE		UINT8
/*TODO*///	#undef TYPE
/*TODO*///	#undef DRAW_FUNC
/*TODO*///	
/*TODO*///	#undef FULL_DRAW
/*TODO*///	
/*TODO*///	#else
/*TODO*///	
/*TODO*///	/***************************************************************************
/*TODO*///	
/*TODO*///	  Road drawing routine
/*TODO*///	
/*TODO*///	***************************************************************************/
/*TODO*///	
/*TODO*///	static void DRAW_FUNC(struct osd_bitmap *bitmap)
/*TODO*///	{
/*TODO*///		TYPE *base = &((TYPE *)bitmap.line[starty])[startx];
/*TODO*///		UINT32 sprite_buffer[VIEW_WIDTH + 256];
/*TODO*///	
/*TODO*///		UINT8 *overall_priority_base = &overall_priority[(turbo_fbpla & 8) << 6];
/*TODO*///		UINT8 *sprite_priority_base = &sprite_priority[(turbo_fbpla & 7) << 7];
/*TODO*///		UINT8 *road_gfxdata_base = &road_gfxdata[(turbo_opc << 5) & 0x7e0];
/*TODO*///		UINT16 *road_palette_base = &road_expanded_palette[(turbo_fbcol & 1) << 4];
/*TODO*///	
/*TODO*///		int dx = deltax, dy = deltay, rowsize = (bitmap.line[1] - bitmap.line[0]) * 8 / bitmap.depth;
/*TODO*///		UINT16 *colortable;
/*TODO*///		int x, y, i;
/*TODO*///	
/*TODO*///		/* expand the appropriate delta */
/*TODO*///		if (Machine.orientation & ORIENTATION_SWAP_XY)
/*TODO*///			dx *= rowsize;
/*TODO*///		else
/*TODO*///			dy *= rowsize;
/*TODO*///	
/*TODO*///		/* determine the color offset */
/*TODO*///		colortable = &Machine.pens[(turbo_fbcol & 6) << 6];
/*TODO*///	
/*TODO*///		/* loop over rows */
/*TODO*///		for (y = 4; y < VIEW_HEIGHT - 4; y++, base += dy)
/*TODO*///		{
/*TODO*///			int sel, coch, babit, slipar_acciar, area, area1, area2, area3, area4, area5, road = 0;
/*TODO*///			UINT32 *sprite_data = sprite_buffer;
/*TODO*///			TYPE *dest = base;
/*TODO*///	
/*TODO*///			/* compute the Y sum between opa and the current scanline (p. 141) */
/*TODO*///			int va = (y + turbo_opa) & 0xff;
/*TODO*///	
/*TODO*///			/* the upper bit of OPC inverts the road */
/*TODO*///			if (!(turbo_opc & 0x80)) va ^= 0xff;
/*TODO*///	
/*TODO*///			/* clear the sprite buffer and draw the road sprites */
/*TODO*///			memset(sprite_buffer, 0, VIEW_WIDTH * sizeof(UINT32));
/*TODO*///			draw_road_sprites(sprite_buffer, y);
/*TODO*///	
/*TODO*///			/* loop over 8-pixel chunks */
/*TODO*///			dest += dx * 8;
/*TODO*///			sprite_data += 8;
/*TODO*///			for (x = 8; x < VIEW_WIDTH; x += 8)
/*TODO*///			{
/*TODO*///				int area5_buffer = road_gfxdata_base[0x4000 + (x >> 3)];
/*TODO*///				UINT8 back_data = videoram.read((y / 8) * 32 + (x / 8) - 33);
/*TODO*///				UINT16 backbits_buffer = back_expanded_data[(back_data << 3) | (y & 7)];
/*TODO*///	
/*TODO*///				/* loop over columns */
/*TODO*///				for (i = 0; i < 8; i++, dest += dx)
/*TODO*///				{
/*TODO*///					UINT32 sprite = *sprite_data++;
/*TODO*///	
/*TODO*///					/* compute the X sum between opb and the current column; only the carry matters (p. 141) */
/*TODO*///					int carry = (x + i + turbo_opb) >> 8;
/*TODO*///	
/*TODO*///					/* the carry selects which inputs to use (p. 141) */
/*TODO*///					if (carry != 0)
/*TODO*///					{
/*TODO*///						sel	 = turbo_ipb;
/*TODO*///						coch = turbo_ipc >> 4;
/*TODO*///					}
/*TODO*///					else
/*TODO*///					{
/*TODO*///						sel	 = turbo_ipa;
/*TODO*///						coch = turbo_ipc & 15;
/*TODO*///					}
/*TODO*///	
/*TODO*///					/* at this point we also compute area5 (p. 141) */
/*TODO*///					area5 = (area5_buffer >> 3) & 0x10;
/*TODO*///					area5_buffer <<= 1;
/*TODO*///	
/*TODO*///					/* now look up the rest of the road bits (p. 142) */
/*TODO*///					area1 = road_gfxdata[0x0000 | ((sel & 15) << 8) | va];
/*TODO*///					area1 = ((area1 + x + i) >> 8) & 0x01;
/*TODO*///					area2 = road_gfxdata[0x1000 | ((sel & 15) << 8) | va];
/*TODO*///					area2 = ((area2 + x + i) >> 7) & 0x02;
/*TODO*///					area3 = road_gfxdata[0x2000 | ((sel >> 4) << 8) | va];
/*TODO*///					area3 = ((area3 + x + i) >> 6) & 0x04;
/*TODO*///					area4 = road_gfxdata[0x3000 | ((sel >> 4) << 8) | va];
/*TODO*///					area4 = ((area4 + x + i) >> 5) & 0x08;
/*TODO*///	
/*TODO*///					/* compute the final area value and look it up in IC18/PR1115 (p. 144) */
/*TODO*///					area = area5 | area4 | area3 | area2 | area1;
/*TODO*///					babit = road_enable_collide[area] & 0x07;
/*TODO*///	
/*TODO*///					/* note: SLIPAR is 0 on the road surface only */
/*TODO*///					/*		 ACCIAR is 0 on the road surface and the striped edges only */
/*TODO*///					slipar_acciar = road_enable_collide[area] & 0x30;
/*TODO*///					if (!road && (slipar_acciar & 0x20))
/*TODO*///					{
/*TODO*///						road = 1;
/*TODO*///						draw_offroad_sprites(sprite_buffer, x + i + 2, y);
/*TODO*///					}
/*TODO*///	
/*TODO*///					/* perform collision detection here */
/*TODO*///					turbo_collision |= collision_map[((sprite >> 24) & 7) | (slipar_acciar >> 1)];
/*TODO*///	
/*TODO*///					/* we only need to continue if we're actually drawing */
/*TODO*///					if (FULL_DRAW != 0)
/*TODO*///					{
/*TODO*///						int bacol, red, grn, blu, priority, backbits, mx;
/*TODO*///	
/*TODO*///						/* also use the coch value to look up color info in IC13/PR1114 and IC21/PR1117 (p. 144) */
/*TODO*///						bacol = road_palette_base[coch & 15];
/*TODO*///	
/*TODO*///						/* at this point, do the character lookup */
/*TODO*///						backbits = backbits_buffer & 3;
/*TODO*///						backbits_buffer >>= 2;
/*TODO*///						backbits = back_palette[backbits | (back_data & 0xfc)];
/*TODO*///	
/*TODO*///						/* look up the sprite priority in IC11/PR1122 */
/*TODO*///						priority = sprite_priority_base[sprite >> 25];
/*TODO*///	
/*TODO*///						/* use that to look up the overall priority in IC12/PR1123 */
/*TODO*///						mx = overall_priority_base[(priority & 7) | ((sprite >> 21) & 8) | ((back_data >> 3) & 0x10) | ((backbits << 2) & 0x20) | (babit << 6)];
/*TODO*///	
/*TODO*///						/* the input colors consist of a mix of sprite, road and 1's & 0's */
/*TODO*///						red = 0x040000 | ((bacol & 0x001f) << 13) | ((backbits & 1) << 12) | ((sprite <<  4) & 0x0ff0);
/*TODO*///						grn = 0x080000 | ((bacol & 0x03e0) <<  9) | ((backbits & 2) << 12) | ((sprite >>  3) & 0x1fe0);
/*TODO*///						blu = 0x100000 | ((bacol & 0x7c00) <<  5) | ((backbits & 4) << 12) | ((sprite >> 10) & 0x3fc0);
/*TODO*///	
/*TODO*///						/* we then go through a muxer; normally these values are inverted, but */
/*TODO*///						/* we've already taken care of that when we generated the palette */
/*TODO*///						red = (red >> mx) & 0x10;
/*TODO*///						grn = (grn >> mx) & 0x20;
/*TODO*///						blu = (blu >> mx) & 0x40;
/*TODO*///						*dest = colortable[mx | red | grn | blu];
/*TODO*///					}
/*TODO*///				}
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	
/*TODO*///	#endif
}
