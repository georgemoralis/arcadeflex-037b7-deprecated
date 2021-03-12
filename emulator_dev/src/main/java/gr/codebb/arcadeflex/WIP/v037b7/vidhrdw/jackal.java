/***************************************************************************

  vidhrdw.c

  Written by Kenneth Lin (kenneth_lin@ai.vancouver.bc.ca)

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.common.libc.cstring.memcpy;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.common.libc.expressions.sizeof;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;

import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.v056.mame.timer.*;
import static gr.codebb.arcadeflex.v056.mame.timerH.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.copybitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;

public class jackal
{
	
	public static UBytePtr jackal_scrollram=new UBytePtr(), jackal_videoctrl=new UBytePtr();
	
	
	public static int TOTAL_COLORS(int gfxn){ return (Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity); }
	public static void COLOR(char[] colortable, int gfxn, int offs, int value){ colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start + offs] = (char) value; }
        
	public static VhConvertColorPromPtr jackal_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int i;
		
	
	
		for (i = 0;i < TOTAL_COLORS(1);i++)
		{
			COLOR(colortable,1,i,color_prom.read() & 0x0f);
			color_prom.inc();
		}
		for (i = 0;i < TOTAL_COLORS(2);i++)
		{
			COLOR(colortable,2,i,(color_prom.read() & 0x0f) + 16);
			color_prom.inc();
		}
	} };
	
	
	
	public static VhStartPtr jackal_vh_start = new VhStartPtr() { public int handler() 
	{
		videoram_size[0] = 0x400;
	
		dirtybuffer = null;
		tmpbitmap = null;
	
		if ((dirtybuffer = new char[videoram_size[0]]) == null)
		{
			return 1;
		}
		memset(dirtybuffer,1,videoram_size[0]);
		if ((tmpbitmap = bitmap_alloc(Machine.drv.screen_width,Machine.drv.screen_height)) == null)
		{
			dirtybuffer=null;
			return 1;
		}
		return 0;
	} };
	
	
	public static VhStopPtr jackal_vh_stop = new VhStopPtr() { public void handler() 
	{
		dirtybuffer=null;
		bitmap_free(tmpbitmap);
	
		dirtybuffer = null;
		tmpbitmap = null;
	} };
	
	
	
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr jackal_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		UBytePtr sr, ss;
		int offs,i;
		UBytePtr RAM = (memory_region(REGION_CPU1));
	
	
		if (palette_recalc() != null)
		{
			memset(dirtybuffer,1,videoram_size[0]);
		}
	
		jackal_scrollram = new UBytePtr(RAM, 0x0020);
		colorram = new UBytePtr(RAM, 0x2000);
		videoram = new UBytePtr(RAM, 0x2400);
	
		spriteram_size[0] = 0x500;
	
		if ((jackal_videoctrl.read(0x03) & 0x08) != 0)
		{
			sr = new UBytePtr(RAM, 0x03800);	// Sprite 2
			ss = new UBytePtr(RAM, 0x13800);	// Additional Sprite 2
		}
		else
		{
			sr = new UBytePtr(RAM, 0x03000);	// Sprite 1
			ss = new UBytePtr(RAM, 0x13000);	// Additional Sprite 1
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
	
				drawgfx(tmpbitmap,Machine.gfx[0],
					videoram.read(offs)+ ((colorram.read(offs)& 0xc0) << 2) + ((colorram.read(offs)& 0x30) << 6),
					0,//colorram.read(offs)& 0x0f, there must be a PROM like in Contra
					colorram.read(offs)& 0x10,colorram.read(offs)& 0x20,
					8*sx,8*sy,
					null,TRANSPARENCY_NONE,0);
			}
		}
	
	
		/* copy the temporary bitmap to the screen */
		{
			int h_scroll_num = 0, v_scroll_num = 0;
			int[] h_scroll=new int[32], v_scroll=new int[32];
	
			if ((jackal_videoctrl.read(2) & 0x08) != 0)
			{
				h_scroll_num = 32;
				for (i = 0;i < 32;i++)
					h_scroll[i] = -(jackal_scrollram.read(i));
			}
	
			if ((jackal_videoctrl.read(2) & 0x04) != 0)
			{
				v_scroll_num = 32;
				for (i = 0;i < 32;i++)
					v_scroll[i] = -(jackal_scrollram.read(i));
			}
	
			if (jackal_videoctrl.read(0) != 0)
			{
				v_scroll_num = 1;
				v_scroll[0] = -(jackal_videoctrl.read(0));
			}
	
			if (jackal_videoctrl.read(1) != 0)
			{
				h_scroll_num = 1;
				h_scroll[0] = -(jackal_videoctrl.read(1));
			}
	
			if ((h_scroll_num == 0) && (v_scroll_num == 0))
				copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.visible_area,TRANSPARENCY_NONE,0);
			else
				copyscrollbitmap(bitmap,tmpbitmap,h_scroll_num,h_scroll,v_scroll_num,v_scroll,Machine.visible_area,TRANSPARENCY_NONE,0);
		}
	
		/* Draw the sprites. */
		{
			int sr1, sr2, sr3, sr4, sr5;
			int spritenum, sx, sy, color;
			int sn1, sn2, sp, flipx, flipy;
	
			for ( offs = 0; offs < 0x0F5; /* offs += 5 */ )
			{
				sn1 = ss.read(offs++); // offs+0
				sn2 = ss.read(offs++); // offs+1
				sy  = ss.read(offs++); // offs+2
				sx  = ss.read(offs++); // offs+3
				sp  = ss.read(offs++); // offs+4
	
				flipx = sp & 0x20;
				flipy = sp & 0x40;
				color = ((sn2 & 0xf0)>>4);
	
				if ( (sp & 0xC) == 0 )
				{
					spritenum = sn1 + ((sn2 & 0x3) << 8);
	
					if (sy > 0xF0) sy = sy - 256;
					if ((sp & 0x01) != 0) sx = sx - 256;
	
					if ((sp & 0x10) != 0)
					{
						if ( (sx > -16) || (sx < 0xF0) )
						{
							drawgfx(bitmap,Machine.gfx[2],
								spritenum,
								color,
								flipx,flipy,
								flipx!=0?sx+16:sx, flipy!=0?sy+16:sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[2],
								spritenum+1,
								color,
								flipx,flipy,
								flipx!=0?sx:sx+16, flipy!=0?sy+16:sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[2],
								spritenum+2,
								color,
								flipx,flipy,
								flipx!=0?sx+16:sx, flipy!=0?sy:sy+16,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[2],
								spritenum+3,
								color,
								flipx,flipy,
								flipx!=0?sx:sx+16, flipy!=0?sy:sy+16,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
					}
					else
					{
						if ( (sx > -8) || (sx < 0xF0) )
						{
							drawgfx(bitmap,Machine.gfx[2],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
					}
				}
				else if ( (sx < 0xF0) && (sp & 0x01)==0 )
				{
					spritenum = sn1*4 + ((sn2 & (8+4)) >> 2) + ((sn2 & (2+1)) << 10);
	
					if ((sp & 0x0C) == 0x0C)
					{
						drawgfx(bitmap,Machine.gfx[4],
							spritenum,
							color,
							flipx,flipy,
							sx,sy,
							Machine.visible_area,TRANSPARENCY_PEN,0);
					}
					if ((sp & 0x0C) == 0x08)
					{
						drawgfx(bitmap,Machine.gfx[4],
							spritenum,
							color,
							flipx,flipy,
							sx,sy,
							Machine.visible_area,TRANSPARENCY_PEN,0);
						drawgfx(bitmap,Machine.gfx[4],
							spritenum - 2,
							color,
							flipx,flipy,
							sx,sy+8,
							Machine.visible_area,TRANSPARENCY_PEN,0);
					}
					if ((sp & 0x0C) == 0x04)
					{
						drawgfx(bitmap,Machine.gfx[4],
							spritenum,
							color,
							flipx,flipy,
							sx,sy,
							Machine.visible_area,TRANSPARENCY_PEN,0);
						drawgfx(bitmap,Machine.gfx[4],
							spritenum + 1,
							color,
							flipx,flipy,
							sx+8,sy,
							Machine.visible_area,TRANSPARENCY_PEN,0);
					}
				}
			}
	
			for (offs = 0; offs < 0x11D; offs += 5)
			{
				if ( (sr.read(offs+2) < 0xF0) && (sr.read(offs+4) & 0x01)==0 )
				{
					sr1 = sr.read(offs);
					sr2 = sr.read(offs+1);
					sr3 = sr.read(offs+2);
					sr4 = sr.read(offs+3);
					sr5 = sr.read(offs+4);
	
					sy = sr3;
					sx = sr4;
	
					flipx = sr5 & 0x20;
					flipy = sr5 & 0x40;
					color = ((sr2 & 0xf0)>>4);
	
					spritenum = sr1 + ((sr2 & 0x3) << 8);
	
					if ((sr5 & 0xC) != 0)    /* half sized sprite */
					{
	
						spritenum = sr1*4 + ((sr2 & (8+4)) >> 2) + ((sr2 & (2+1)) << 10);
	
						if ((sr5 & 0x0C) == 0x0C)
						{
							drawgfx(bitmap,Machine.gfx[3],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
						if ((sr5 & 0x0C) == 0x08)
						{
							drawgfx(bitmap,Machine.gfx[3],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[3],
								spritenum - 2,
								color,
								flipx,flipy,
								sx,sy+8,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
						if ((sr5 & 0x0C) == 0x04)
						{
							drawgfx(bitmap,Machine.gfx[3],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[3],
								spritenum + 1,
								color,
								flipx,flipy,
								sx+8,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
	
					}
					else
					{
						if ((sr5 & 0x10) != 0)
						{
							drawgfx(bitmap,Machine.gfx[1],
								spritenum,
								color,
								flipx,flipy,
								flipx!=0?sx+16:sx, flipy!=0?sy+16:sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[1],
								spritenum+1,
								color,
								flipx,flipy,
								flipx!=0?sx:sx+16, flipy!=0?sy+16:sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[1],
								spritenum+2,
								color,
								flipx,flipy,
								flipx!=0?sx+16:sx, flipy!=0?sy:sy+16,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[1],
								spritenum+3,
								color,
								flipx,flipy,
								flipx!=0?sx:sx+16, flipy!=0?sy:sy+16,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
						else
							drawgfx(bitmap,Machine.gfx[1],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
					}
				}
			}
	
			for (offs = 0x4F1; offs >= 0x11D; offs -= 5)
			{
				if ( (sr.read(offs+2) < 0xF0) && (sr.read(offs+4) & 0x01)==0 )
				{
					sr1 = sr.read(offs);
					sr2 = sr.read(offs+1);
					sr3 = sr.read(offs+2);
					sr4 = sr.read(offs+3);
					sr5 = sr.read(offs+4);
	
					sy = sr3;
					sx = sr4;
	
					flipx = sr5 & 0x20;
					flipy = sr5 & 0x40;
					color = ((sr2 & 0xf0)>>4);
	
					if ((sr.read(offs+4) & 0xC)!=0)    /* half sized sprite */
					{
	
						spritenum = sr1*4 + ((sr2 & (8+4)) >> 2) + ((sr2 & (2+1)) << 10);
	
						if ((sr5 & 0x0C) == 0x0C)
						{
							drawgfx(bitmap,Machine.gfx[3],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
						if ((sr5 & 0x0C) == 0x08)
						{
							drawgfx(bitmap,Machine.gfx[3],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[3],
								spritenum - 2,
								color,
								flipx,flipy,
								sx,sy+8,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
						if ((sr5 & 0x0C) == 0x04)
						{
							drawgfx(bitmap,Machine.gfx[3],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[3],
								spritenum + 1,
								color,
								flipx,flipy,
								sx+8,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
	
					}
					else
					{
						spritenum = sr1 + ((sr2 & 0x3) << 8);
	
						if ((sr5 & 0x10) != 0)
						{
							drawgfx(bitmap,Machine.gfx[1],
								spritenum,
								color,
								flipx,flipy,
								flipx!=0?sx+16:sx, flipy!=0?sy+16:sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[1],
								spritenum+1,
								color,
								flipx,flipy,
								flipx!=0?sx:sx+16, flipy!=0?sy+16:sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[1],
								spritenum+2,
								color,
								flipx,flipy,
								flipx!=0?sx+16:sx, flipy!=0?sy:sy+16,
								Machine.visible_area,TRANSPARENCY_PEN,0);
							drawgfx(bitmap,Machine.gfx[1],
								spritenum+3,
								color,
								flipx,flipy,
								flipx!=0?sx:sx+16, flipy!=0?sy:sy+16,
								Machine.visible_area,TRANSPARENCY_PEN,0);
						}
						else
							drawgfx(bitmap,Machine.gfx[1],
								spritenum,
								color,
								flipx,flipy,
								sx,sy,
								Machine.visible_area,TRANSPARENCY_PEN,0);
					}
				}
			}
		}
	} };
}
