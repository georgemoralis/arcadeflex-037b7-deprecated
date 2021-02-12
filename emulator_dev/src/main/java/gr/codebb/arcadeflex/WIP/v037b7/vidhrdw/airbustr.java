/**************************************************************************

								Air Buster
						    (C) 1990  Kaneko

				    driver by Luca Elia (eliavit@unina.it)

[Screen]
 	Size:				256 x 256
	Colors:				256 x 3
	Color Space:		32R x 32G x 32B

[Scrolling layers]
	Number:				2
	Size:				512 x 512
	Scrolling:			X,Y
	Tiles Size:			16 x 16
	Tiles Number:		0x1000
	Colors:				256 x 2	(0-511)
	Format:
				Offset:		0x400    0x000
				Bit:		fedc---- --------	Color
							----ba98 76543210	Code

[Sprites]
	On Screen:			256 x 2
	In ROM:				0x2000
	Colors:				256		(512-767)
	Format:				See Below


**************************************************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.drivers.airbustr.flipscreen;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.logerror;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.paletteH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;

public class airbustr
{
	
	static struct_tilemap bg_tilemap,fg_tilemap;
	
	/* Variables that drivers has access to */
	public static UBytePtr airbustr_bgram=new UBytePtr(), airbustr_fgram=new UBytePtr();
	
	/* Variables defined in drivers */
	//extern UBytePtr spriteram;
	//extern int flipscreen;
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int attr = airbustr_fgram.read(tile_index + 0x400);
		SET_TILE_INFO(	0,
						airbustr_fgram.read(tile_index) + ((attr & 0x0f) << 8),
						(attr >> 4) + 0);
	} };
	
	public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int attr = airbustr_bgram.read(tile_index + 0x400);
		SET_TILE_INFO(	0,
						airbustr_bgram.read(tile_index) + ((attr & 0x0f) << 8),
						(attr >> 4) + 16);
	} };
	
	
	
	public static VhStartPtr airbustr_vh_start = new VhStartPtr() { public int handler() 
	{
		fg_tilemap = tilemap_create(get_fg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT,16,16,32,32);
		bg_tilemap = tilemap_create(get_bg_tile_info,tilemap_scan_rows,TILEMAP_OPAQUE,     16,16,32,32);
	
		if (fg_tilemap==null || bg_tilemap==null)
			return 1;
	
		fg_tilemap.transparent_pen = 0;
	
		return 0;
	} };
	
	
	public static WriteHandlerPtr airbustr_fgram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (airbustr_fgram.read(offset) != data)
		{
			airbustr_fgram.write(offset, data);
			tilemap_mark_tile_dirty(fg_tilemap,offset & 0x3ff);
		}
	} };
	
	public static WriteHandlerPtr airbustr_bgram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (airbustr_bgram.read(offset) != data)
		{
			airbustr_bgram.write(offset, data);
			tilemap_mark_tile_dirty(bg_tilemap,offset & 0x3ff);
		}
	} };
	
	
	/*	Scroll Registers
	
		Port:
		4		Bg Y scroll, low 8 bits
		6		Bg X scroll, low 8 bits
		8		Fg Y scroll, low 8 bits
		A		Fg X scroll, low 8 bits
	
		C		3		2		1		0		<-Bit
				Bg Y	Bg X	Fg Y	Fg X	<-Scroll High Bits (complemented!)
	*/
        
        static int bg_scrollx, bg_scrolly, fg_scrollx, fg_scrolly, highbits;
	
	public static WriteHandlerPtr airbustr_scrollregs_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
	
                int xoffs, yoffs;
	
		if (flipscreen != 0)	{	xoffs = -0x06a;		yoffs = -0x1ff;}
		else			{	xoffs = -0x094;		yoffs = -0x100;}
	
		switch (offset)		// offset 0 <. port 4
		{
			case 0x00:	fg_scrolly =  data;	break;	// low 8 bits
			case 0x02:	fg_scrollx =  data;	break;
			case 0x04:	bg_scrolly =  data;	break;
			case 0x06:	bg_scrollx =  data;	break;
			case 0x08:	highbits   = ~data;	break;	// complemented high bits
	
			default:	logerror("CPU #2 - port %02X written with %02X - PC = %04X\n", offset, data, cpu_get_pc());
		}
	
		tilemap_set_scrollx(bg_tilemap, 0, ((highbits << 6) & 0x100) + bg_scrollx + xoffs );
		tilemap_set_scrolly(bg_tilemap, 0, ((highbits << 5) & 0x100) + bg_scrolly + yoffs );
		tilemap_set_scrollx(fg_tilemap, 0, ((highbits << 8) & 0x100) + fg_scrollx + xoffs );
		tilemap_set_scrolly(fg_tilemap, 0, ((highbits << 7) & 0x100) + fg_scrolly + yoffs );
	} };
	
	
	
	/*		Sprites
	
	Offset:					Values:
	
	000-0ff					?
	100-1ff					?
	200-2ff					?
	
	300-3ff		7654----	Color Code
				----3---	?
				-----2--	Multi Sprite
				------1-	Y Position High Bit
				-------0	X Position High Bit
	
	400-4ff					X Position Low 8 Bits
	500-5ff					Y Position Low 8 Bits
	600-6ff					Code Low 8 Bits
	
	700-7ff		7-------	Flip X
				-6------	Flip Y
				--5-----	?
				---43217	Code High Bits
	
	*/
	
	static void draw_sprites(osd_bitmap bitmap)
	{
	int i, offs;
	
		/* Let's draw the sprites */
		for (i = 0 ; i < 2 ; i++)
		{
			UBytePtr ram = new UBytePtr(spriteram, i * 0x800);
			int sx = 0;
			int sy = 0;
	
			for ( offs = 0 ; offs < 0x100 ; offs++)
			{
				int attr	=	ram.read(offs + 0x300);
				int x		=	ram.read(offs + 0x400) - ((attr << 8) & 0x100);
				int y		=	ram.read(offs + 0x500) - ((attr << 7) & 0x100);
	
				int gfx		=	ram.read(offs + 0x700);
				int code	=	ram.read(offs + 0x600) + ((gfx & 0x1f) << 8);
				int flipx	=	gfx & 0x80;
				int flipy	=	gfx & 0x40;
	
				/* multi sprite */
				if ((attr & 0x04) != 0)	{ sx += x;		sy += y;}
				else				{ sx  = x;		sy  = y;}
	
				if (flipscreen != 0)
				{
					sx = 240 - sx;		sy = 240 - sy;
					flipx = flipx!=0?0:1;		
                                        flipy = flipy!=0?0:1;
				}
	
				drawgfx(bitmap,Machine.gfx[1],
						code,
						attr >> 4,
						flipx, flipy,
						sx,sy,
						Machine.visible_area,TRANSPARENCY_PEN,0);
	
				/* let's get back to normal to support multi sprites */
				if (flipscreen != 0)	{sx = 240 - sx;		sy = 240 - sy;}
	
			}
		}
	
	}
	
	
	public static VhUpdatePtr airbustr_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
	UBytePtr ram;
	int i, offs;
	
/*TODO*///	#if 0
/*TODO*///	/*
/*TODO*///		Let's show some of the unknown bits:
/*TODO*///		bankswitch reg cpu 0, 1, 2 [& 0xf8!] and sub cpu port 28
/*TODO*///	*/
/*TODO*///	
/*TODO*///		if (keyboard_pressed(KEYCODE_Z))
/*TODO*///		{
/*TODO*///		char buf[80];
/*TODO*///			sprintf(buf,"%02X %02X %02X %02X", u1,u2,u3,u4);
/*TODO*///			usrintf_showmessage(buf);
/*TODO*///		}
/*TODO*///	#endif
	
		tilemap_update(ALL_TILEMAPS);
	
	/* Palette Stuff */
	
		palette_init_used_colors();
	
/*TODO*///	#if 0
/*TODO*///		/* Scrolling Layers */
/*TODO*///		ram = &airbustr_bgram[0x000 + 0x400];	// color code
/*TODO*///		for ( offs = 0 ; offs < 0x100 ; offs++)
/*TODO*///		{
/*TODO*///			int color =	256 * 0 + (ram[offs] & 0xf0);
/*TODO*///			memset(&palette_used_colors[color],PALETTE_COLOR_USED,16);
/*TODO*///		}
/*TODO*///		ram = &airbustr_fgram[0x800 + 0x400];	// color code
/*TODO*///		for ( offs = 0 ; offs < 0x100 ; offs++)
/*TODO*///		{
/*TODO*///			int color =	256 * 1 + (ram[offs] & 0xf0);
/*TODO*///			memset(&palette_used_colors[color+1],PALETTE_COLOR_USED,16-1);
/*TODO*///		}
/*TODO*///	#endif
	
		/* Sprites */
		for (i = 0 ; i < 2 ; i++)
		{
			ram = new UBytePtr(spriteram, i * 0x800 + 0x300);	// color code
			for ( offs = 0 ; offs < 0x100 ; offs++)
			{
				int color = 256 * 2 + (ram.read(offs) & 0xf0);
				memset(new UBytePtr(palette_used_colors, color+1),PALETTE_COLOR_USED,16-1);
			}
		}
	
		if (palette_recalc() != null)	tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
	
		tilemap_render(ALL_TILEMAPS);
	
		tilemap_draw(bitmap,bg_tilemap,0);
		tilemap_draw(bitmap,fg_tilemap,0);
		draw_sprites(bitmap);
	} };
}
