/***************************************************************************

  vidhrdw.c

  Functions to emulate the video hardware of the machine.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809.m6809H.M6809_INT_FIRQ;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.fillbitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.tms34061.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.tms34061H.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.plot_pixel;

public class capbowl
{
	
	public static UBytePtr capbowl_rowaddress=new UBytePtr();
	
	public static UBytePtr raw_video_ram=new UBytePtr();
	static int[]  color_count=new int[4096];
	static int[] dirty_row=new int[256];
	
	static int max_col, max_row, max_col_offset;
	
	public static final int PAL_SIZE  = 0x20;
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	public static InterruptPtr capbowl_vertical_interrupt = new InterruptPtr() { public int handler() 
	{
		return M6809_INT_FIRQ;
	} };
	
	
	
	public static VhStartPtr capbowl_vh_start = new VhStartPtr() { public int handler() 
	{
		int i;
	
		if ((raw_video_ram = new UBytePtr(256 * 256)) == null)
		{
			return 1;
		}
	
		// Initialize TMS34061 emulation
	    if (TMS34061_start(tms34061_interface) != 0)
		{
			raw_video_ram = null;
			return 1;
		}
	
		max_row = Machine.visible_area.max_y;
		max_col = Machine.visible_area.max_x;
		max_col_offset = (max_col + 1) / 2 + PAL_SIZE;
	
		// Initialize color areas. The screen is blank
		memset(raw_video_ram, 0, 256*256);
		palette_init_used_colors();
		memset(color_count, 0, color_count.length);
		memset(dirty_row, 1, dirty_row.length);
	
		for (i = 0; i < max_row * 16; i+=16)
		{
			palette_used_colors.write(i, PALETTE_COLOR_USED);
			color_count[i] = max_col + 1;  // All the pixels are pen 0
		}
	
		return 0;
	} };
	
	
	
	/***************************************************************************
	
	  Stop the video hardware emulation.
	
	***************************************************************************/
	public static VhStopPtr capbowl_vh_stop = new VhStopPtr() { public void handler() 
	{
		raw_video_ram=null;
	
		TMS34061_stop();
	} };
	
	
	/***************************************************************************
	
	  TMS34061 callbacks
	
	***************************************************************************/
	
	static TMS34061_getfunction_t capbowl_tms34061_getfunction=new TMS34061_getfunction_t() {
            @Override
            public int handler(int offset) {
                /* The function inputs (FS0-FS2) are hooked up the following way:
	
		   FS0 = A8
		   FS1 = A9
		   FS2 = grounded
		 */
	
		return (offset >> 8) & 0x03;
            }
        };
        
	
	static TMS34061_getrowaddress_t capbowl_tms34061_getrowaddress = new TMS34061_getrowaddress_t() {
            @Override
            public int handler(int offset) {
                /* Row address (RA0-RA8) is not dependent on the offset */
		return capbowl_rowaddress.read();
            }
        };
        
	
	static TMS34061_getcoladdress_t capbowl_tms34061_getcoladdress = new TMS34061_getcoladdress_t() {
            @Override
            public int handler(int offset) {
                /* Column address (CA0-CA8) is hooked up the A0-A7, with A1 being inverted
		   during register access. CA8 is ignored */
		int col = (offset & 0xff);
	
		if ((offset & 0x300)==0)
		{
			col ^= 0x02;
		}
	
		return col;
            }
        };
        
	
	static TMS34061_setpixel_t capbowl_tms34061_setpixel = new TMS34061_setpixel_t() {
            @Override
            public int handler(int col, int row, int pixel) {
                int off = ((row << 8) | col);
		int penstart = row << 4;
	
		int oldpixel = raw_video_ram.read(off);
	
		raw_video_ram.write(off, pixel);
	
		if (row > max_row || col >= max_col_offset) return 0;
	
		if (col >= PAL_SIZE)
		{
			int oldpen1 = penstart | (oldpixel >> 4);
			int oldpen2 = penstart | (oldpixel & 0x0f);
			int newpen1 = penstart | (pixel >> 4);
			int newpen2 = penstart | (pixel & 0x0f);
	
			if (oldpen1 != newpen1)
			{
				dirty_row[row] = 1;
	
				color_count[oldpen1]--;
				if (color_count[oldpen1]==0) 
                                    palette_used_colors.write(oldpen1, PALETTE_COLOR_UNUSED);
	
				color_count[newpen1]++;
				palette_used_colors.write(newpen1, PALETTE_COLOR_USED);
			}
	
			if (oldpen2 != newpen2)
			{
				dirty_row[row] = 1;
	
				color_count[oldpen2]--;
				if (color_count[oldpen2]==0) palette_used_colors.write(oldpen2, PALETTE_COLOR_UNUSED);
	
				color_count[newpen2]++;
				palette_used_colors.write(newpen2, PALETTE_COLOR_USED);
			}
		}
		else
		{
			/* Offsets 0-1f are the palette */
	
			int r = (raw_video_ram.read(off & ~1) & 0x0f);
			int g = (raw_video_ram.read(off |  1) >> 4);
			int b = (raw_video_ram.read(off |  1) & 0x0f);
			r = (r << 4) + r;
			g = (g << 4) + g;
			b = (b << 4) + b;
	
			palette_change_color(penstart | (col >> 1),r,g,b);
		}
                return 0;
            }
        };
        
	
	static TMS34061_getpixel_t capbowl_tms34061_getpixel = new TMS34061_getpixel_t() {
            @Override
            public int handler(int col, int row) {
                return raw_video_ram.read(row << 8 | col);
            }
        };
        
        static TMS34061interface tms34061_interface = new TMS34061interface
	(
		capbowl_tms34061_getfunction,
		capbowl_tms34061_getrowaddress,
		capbowl_tms34061_getcoladdress,
		capbowl_tms34061_getpixel,
		capbowl_tms34061_setpixel,
		0,
                capbowl_vertical_interrupt  /* Vertical interrupt causes a FIRQ */
	);
        
	/***************************************************************************
	
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function, it will be called by
	  the main emulation engine.
	
	***************************************************************************/
	public static VhUpdatePtr capbowl_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int col, row;
		UBytePtr remapped;
	
	
		if (full_refresh != 0)
		{
			for (row = 0; row <= max_row; row++)  dirty_row[row] = 1;
		}
	
		if (TMS34061_display_blanked() != 0)
		{
			fillbitmap(bitmap,palette_transparent_pen,Machine.visible_area);
			return;
		}
	
		if ((remapped = palette_recalc()) != null)
		{
			for (row = 0; row <= max_row; row++)
			{
				if (dirty_row[row] == 0)
				{
					int i;
	
					for (i = 0;i < 16;i++)
					{
						if (remapped.read(16 * row + i) != 0)
						{
							dirty_row[row] = 1;
							break;
						}
					}
				}
			}
		}
	
		for (row = 0; row <= max_row; row++)
		{
			if (dirty_row[row] != 0)
			{
				int col1 = 0;
				int row1 = (row << 8 | PAL_SIZE);
				int row2 =  row << 4;
	
				dirty_row[row] = 0;
	
				for (col = PAL_SIZE; col < max_col_offset; col++)
				{
					int pixel = raw_video_ram.read(row1++);
	
					plot_pixel.handler(bitmap, col1++,row,Machine.pens[row2 | (pixel >> 4)  ]);
					plot_pixel.handler(bitmap, col1++,row,Machine.pens[row2 | (pixel & 0x0f)]);
				}
			}
		}
	} };
}
