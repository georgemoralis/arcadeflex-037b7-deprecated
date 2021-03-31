/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;

public class cloak
{
	
	
	static osd_bitmap tmpbitmap2,charbitmap;
	static int x,y,bmap;
	static UBytePtr tmpvideoram=new UBytePtr(),tmpvideoram2=new UBytePtr();
	
	
	
	
	/***************************************************************************
	
	  CLOAK & DAGGER uses RAM to dynamically
	  create the palette. The resolution is 9 bit (3 bits per gun). The palette
	  contains 64 entries, but it is accessed through a memory windows 128 bytes
	  long: writing to the first 64 bytes sets the msb of the red component to 0,
	  while writing to the last 64 bytes sets it to 1.
	
	  Colors 0-15  Character mapped graphics
	  Colors 16-31 Bitmapped graphics (maybe 8 colors per bitmap?)
	  Colors 32-47 Sprites
	  Colors 48-63 not used
	
	  I don't know the exact values of the resistors between the RAM and the
	  RGB output, I assumed the usual ones.
	  bit 8 -- inverter -- 220 ohm resistor  -- RED
	        -- inverter -- 470 ohm resistor  -- RED
	        -- inverter -- 1  kohm resistor  -- RED
	        -- inverter -- 220 ohm resistor  -- GREEN
	        -- inverter -- 470 ohm resistor  -- GREEN
	        -- inverter -- 1  kohm resistor  -- GREEN
	        -- inverter -- 220 ohm resistor  -- BLUE
	        -- inverter -- 470 ohm resistor  -- BLUE
	  bit 0 -- inverter -- 1  kohm resistor  -- BLUE
	
	***************************************************************************/
	public static WriteHandlerPtr cloak_paletteram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int r,g,b;
		int bit0,bit1,bit2;
	
	
		/* a write to offset 64-127 means to set the msb of the red component */
		data |= (offset & 0x40) << 2;
	
		r = (~data & 0x1c0) >> 6;
		g = (~data & 0x038) >> 3;
		b = (~data & 0x007);
	
		bit0 = (r >> 0) & 0x01;
		bit1 = (r >> 1) & 0x01;
		bit2 = (r >> 2) & 0x01;
		r = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
		bit0 = (g >> 0) & 0x01;
		bit1 = (g >> 1) & 0x01;
		bit2 = (g >> 2) & 0x01;
		g = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
		bit0 = (b >> 0) & 0x01;
		bit1 = (b >> 1) & 0x01;
		bit2 = (b >> 2) & 0x01;
		b = 0x21 * bit0 + 0x47 * bit1 + 0x97 * bit2;
	
		palette_change_color(offset & 0x3f,r,g,b);
	} };
	
	
	public static WriteHandlerPtr cloak_clearbmp_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		bmap = data & 1;
		if ((data & 2) != 0)	/* clear */
		{
			if (bmap != 0)
			{
				fillbitmap(tmpbitmap, Machine.pens[16], Machine.visible_area);
				memset(tmpvideoram, 0, 256*256);
			}
			else
			{
				fillbitmap(tmpbitmap2, Machine.pens[16], Machine.visible_area);
				memset(tmpvideoram2, 0, 256*256);
			}
		}
	} };
	
	
	static void adjust_xy(int offset)
	{
		switch(offset)
		{
		case 0x00:  x--; y++; break;
		case 0x01:       y--; break;
		case 0x02:  x--;      break;
		case 0x04:  x++; y++; break;
		case 0x05:  	 y++; break;
		case 0x06:  x++;      break;
		}
	}
	
	
	public static ReadHandlerPtr graph_processor_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		int ret;
	
		if (bmap != 0)
		{
			ret = tmpvideoram2.read(y*256+x);
		}
		else
		{
                    	ret = tmpvideoram.read(y*256+x);
		}
	
		adjust_xy(offset);
	
		return ret;
	} };
	
	
	public static WriteHandlerPtr graph_processor_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int col;
	
		switch (offset)
		{
		case 0x03:  x=data; break;
		case 0x07:  y=data; break;
		default:
			col = data & 0x07;
	
			if (bmap != 0)
			{
				plot_pixel.handler(tmpbitmap, (x-6)&0xff, y, Machine.pens[16 + col]);
				tmpvideoram.write(y*256+x, col);
			}
			else
			{
				plot_pixel.handler(tmpbitmap2, (x-6)&0xff, y, Machine.pens[16 + col]);
				tmpvideoram2.write(y*256+x, col);
			}
	
			adjust_xy(offset);
			break;
		}
	} };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	public static VhStartPtr cloak_vh_start = new VhStartPtr() { public int handler() 
	{
		if ((tmpbitmap = bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
			return 1;
	
		if ((charbitmap = bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			cloak_vh_stop.handler();
			return 1;
		}
	
		if ((tmpbitmap2 = bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			cloak_vh_stop.handler();
			return 1;
		}
	
		if ((dirtybuffer = new char[videoram_size[0]]) == null)
		{
			cloak_vh_stop.handler();
			return 1;
		}
		memset(dirtybuffer,1,videoram_size[0]);
	
		if ((tmpvideoram = new UBytePtr(1024*256)) == null)
		{
			cloak_vh_stop.handler();
			return 1;
		}
	
		if ((tmpvideoram2 = new UBytePtr(1024*256)) == null)
		{
			cloak_vh_stop.handler();
			return 1;
		}
	
		return 0;
	} };
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStopPtr cloak_vh_stop = new VhStopPtr() { public void handler() 
	{
		if (charbitmap != null)  bitmap_free(charbitmap);
		if (tmpbitmap2 != null)  bitmap_free(tmpbitmap2);
		if (tmpbitmap != null)   bitmap_free(tmpbitmap);
		if (dirtybuffer != null) dirtybuffer=null;
		if (tmpvideoram != null) tmpvideoram=null;
		if (tmpvideoram2 != null) tmpvideoram2=null;
	} };
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	static void refresh_bitmaps()
	{
		int lx,ly;
	
		for (ly = 0; ly < 256; ly++)
		{
			for (lx = 0; lx < 256; lx++)
			{
				plot_pixel.handler(tmpbitmap,  (lx-6)&0xff, ly, Machine.pens[16 + tmpvideoram.read(ly*256+lx)]);
				plot_pixel.handler(tmpbitmap2, (lx-6)&0xff, ly, Machine.pens[16 + tmpvideoram2.read(ly*256+lx)]);
			}
		}
	}
	
	
	public static VhUpdatePtr cloak_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int offs;
	
	
		palette_used_colors.write(16, PALETTE_COLOR_TRANSPARENT);
		if (palette_recalc() != null)
		{
			memset(dirtybuffer, 1, videoram_size[0]);
	
			refresh_bitmaps();
		}
	
		/* for every character in the Video RAM, check if it has been modified */
		/* since last time and update it accordingly. */
		for (offs = videoram_size[0] - 1;offs >= 0;offs--)
		{
			if (dirtybuffer[offs] != 0)
			{
				int sx,sy;
	
	
				dirtybuffer[offs] = 0;
	
				sx = offs % 32;
				sy = offs / 32;
	
				drawgfx(charbitmap,Machine.gfx[0],
						videoram.read(offs),0,
						0,0,
						8*sx,8*sy,
						Machine.visible_area,TRANSPARENCY_NONE,0);
			}
		}
	
		/* copy the temporary bitmap to the screen */
                copybitmap(bitmap,charbitmap,0,0,0,0,Machine.visible_area,TRANSPARENCY_NONE,0);
		copybitmap(bitmap, bmap!=0 ? tmpbitmap2 : tmpbitmap, 0,0,0,0,Machine.visible_area,TRANSPARENCY_PEN,palette_transparent_pen);
	
	
		/* Draw the sprites */
		for (offs = spriteram_size[0]/4-1; offs >= 0; offs--)
		{
			drawgfx(bitmap,Machine.gfx[1],
					spriteram.read(offs+64)& 0x7f,
					0,
					spriteram.read(offs+64)& 0x80,0,
					spriteram.read(offs+192),240-spriteram.read(offs),
					Machine.visible_area,TRANSPARENCY_PEN,0);
		}
	} };
}
