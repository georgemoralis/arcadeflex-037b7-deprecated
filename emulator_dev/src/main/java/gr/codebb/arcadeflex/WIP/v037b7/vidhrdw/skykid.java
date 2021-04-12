/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;

public class skykid
{
	
	public static UBytePtr skykid_textram=new UBytePtr(), skykid_videoram=new UBytePtr();
	
	static struct_tilemap background;
	static int priority;
	static int flipscreen;
	
	
	/***************************************************************************
	
		Convert the color PROMs into a more useable format.
	
		The palette PROMs are connected to the RGB output this way:
	
		bit 3	-- 220 ohm resistor  -- RED/GREEN/BLUE
				-- 470 ohm resistor  -- RED/GREEN/BLUE
				-- 1  kohm resistor  -- RED/GREEN/BLUE
		bit 0	-- 2.2kohm resistor  -- RED/GREEN/BLUE
	
	***************************************************************************/
	
	public static VhConvertColorPromPtr skykid_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int i;
		int bit0,bit1,bit2,bit3;
		int totcolors = Machine.drv.total_colors;
                int _palette = 0;
                int _colortable = 0;
	
		for (i = 0; i < totcolors; i++)
		{
			/* red component */
			bit0 = (color_prom.read(totcolors*0)>> 0) & 0x01;
			bit1 = (color_prom.read(totcolors*0)>> 1) & 0x01;
			bit2 = (color_prom.read(totcolors*0)>> 2) & 0x01;
			bit3 = (color_prom.read(totcolors*0)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e*bit0 + 0x1f*bit1 + 0x43*bit2 + 0x8f*bit3);
	
			/* green component */
			bit0 = (color_prom.read(totcolors*1)>> 0) & 0x01;
			bit1 = (color_prom.read(totcolors*1)>> 1) & 0x01;
			bit2 = (color_prom.read(totcolors*1)>> 2) & 0x01;
			bit3 = (color_prom.read(totcolors*1)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e*bit0 + 0x1f*bit1 + 0x43*bit2 + 0x8f*bit3);
	
			/* blue component */
			bit0 = (color_prom.read(totcolors*2)>> 0) & 0x01;
			bit1 = (color_prom.read(totcolors*2)>> 1) & 0x01;
			bit2 = (color_prom.read(totcolors*2)>> 2) & 0x01;
			bit3 = (color_prom.read(totcolors*2)>> 3) & 0x01;
			palette[_palette++] = (char) (0x0e*bit0 + 0x1f*bit1 + 0x43*bit2 + 0x8f*bit3);
	
			color_prom.inc();
		}
	
		/* text palette */
		for (i = 0; i < 64*4; i++)
			colortable[_colortable++] = (char) i;
	
		color_prom.inc(2*totcolors);
		/* color_prom now points to the beginning of the lookup table */
	
		/* tiles lookup table */
		for (i = 0; i < 128*4; i++)
			colortable[_colortable++] = (color_prom.readinc());
	
		/* sprites lookup table */
		for (i = 0;i < 64*8;i++)
			colortable[_colortable++] = (color_prom.readinc());
	
	} };
	
	/***************************************************************************
	
		Callbacks for the TileMap code
	
	***************************************************************************/
	
	public static GetTileInfoPtr get_tile_info_bg = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int code = skykid_videoram.read(tile_index);
		int attr = skykid_videoram.read(tile_index+0x800);
	
		SET_TILE_INFO(1, code + 256*(attr & 0x01),((attr & 0x7e) >> 1) | ((attr & 0x01) << 6));
	} };
	
	/***************************************************************************
	
		Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr skykid_vh_start = new VhStartPtr() { public int handler() 
	{
		background = tilemap_create(get_tile_info_bg,tilemap_scan_rows,TILEMAP_OPAQUE,8,8,64,32);
	
		if (background == null)
			return 1;
	
		{
			UBytePtr RAM = new UBytePtr(memory_region(REGION_CPU1));
	
			spriteram	= new UBytePtr(RAM, 0x4f80);
			spriteram_2	= new UBytePtr(RAM, 0x4f80+0x0800);
			spriteram_3	= new UBytePtr(RAM, 0x4f80+0x0800+0x0800);
			spriteram_size[0] = 0x80;
	
			return 0;
		}
	} };
	
	/***************************************************************************
	
		Memory handlers
	
	***************************************************************************/
	
	public static ReadHandlerPtr skykid_videoram_r  = new ReadHandlerPtr() { public int handler(int offset)
	{
		return skykid_videoram.read(offset);
	} };
	
	public static WriteHandlerPtr skykid_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (skykid_videoram.read(offset) != data){
			skykid_videoram.write(offset, data);
			tilemap_mark_tile_dirty(background,offset & 0x7ff);
		}
	} };
	
	public static WriteHandlerPtr skykid_scroll_x_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (flipscreen != 0)
			tilemap_set_scrollx(background, 0, (189 - (offset ^ 1)) & 0x1ff);
		else
			tilemap_set_scrollx(background, 0, ((offset) + 35) & 0x1ff);
	} };
	
	public static WriteHandlerPtr skykid_scroll_y_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (flipscreen != 0)
			tilemap_set_scrolly(background, 0, (261 - offset) & 0xff);
		else
			tilemap_set_scrolly(background, 0, (offset + 27) & 0xff);
	} };
	
	public static WriteHandlerPtr skykid_flipscreen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		priority = data;
		flipscreen = offset;
		tilemap_set_flip(background,flipscreen!=0 ? (TILEMAP_FLIPY | TILEMAP_FLIPX) : 0);
	} };
	
	/***************************************************************************
	
		Display Refresh
	
	***************************************************************************/
	
	static void skykid_draw_sprites(osd_bitmap bitmap)
	{
		int offs;
	
		for (offs = 0; offs < spriteram_size[0]; offs += 2){
			int number = spriteram.read(offs)| ((spriteram_3.read(offs)& 0x80) << 1);
			int color = (spriteram.read(offs+1)& 0x3f);
			int sx = (spriteram_2.read(offs+1)) + 0x100*(spriteram_3.read(offs+1)& 1) - 72;
			int sy = 256 - spriteram_2.read(offs)- 57;
			int flipy = spriteram_3.read(offs)& 0x02;
			int flipx = spriteram_3.read(offs)& 0x01;
			int width, height;
	
			if (flipscreen != 0){
					flipx = flipx!=0?0:1;
					flipy = flipy!=0?0:1;
			}
	
			if (number >= 128*3) continue;
	
			switch (spriteram_3.read(offs)& 0x0c){
				case 0x0c:	/* 2x both ways */
					width = height = 2; number &= (~3); break;
				case 0x08:	/* 2x vertical */
					width = 1; height = 2; number &= (~2); break;
				case 0x04:	/* 2x horizontal */
					width = 2; height = 1; number &= (~1); sy += 16; break;
				default:	/* normal sprite */
					width = height = 1; sy += 16; break;
			}
	
			{
				int x_offset[] = { 0x00, 0x01 };
				int y_offset[] = { 0x00, 0x02 };
				int x,y, ex, ey;
	
				for( y=0; y < height; y++ ){
					for( x=0; x < width; x++ ){
						ex = flipx!=0 ? (width-1-x) : x;
						ey = flipy!=0 ? (height-1-y) : y;
	
						drawgfx(bitmap,Machine.gfx[2+(number >> 7)],
							(number)+x_offset[ex]+y_offset[ey],
							color,
							flipx, flipy,
							sx+x*16,sy+y*16,
							Machine.visible_area,
							TRANSPARENCY_COLOR,255);
					}
				}
			}
		}
	}
	
	public static VhUpdatePtr skykid_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int offs;
	
		tilemap_update(ALL_TILEMAPS);
	
		if (palette_recalc() != null)
			tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
	
		tilemap_render(ALL_TILEMAPS);
	
		tilemap_draw(bitmap,background,0);
		if ((priority & 0xf0) != 0x50)
			skykid_draw_sprites(bitmap);
	
		for (offs = 0x400 - 1; offs > 0; offs--){
			{
				int mx,my,sx,sy;
	
	            mx = offs % 32;
				my = offs / 32;
	
				if (my < 2)	{
					if (mx < 2 || mx >= 30) continue; /* not visible */
					sx = my + 34;
					sy = mx - 2;
				}
				else if (my >= 30){
					if (mx < 2 || mx >= 30) continue; /* not visible */
					sx = my - 30;
					sy = mx - 2;
				}
				else{
					sx = mx + 2;
					sy = my - 2;
				}
				if (flipscreen != 0){
					sx = 35 - sx;
					sy = 27 - sy;
				}
	
				drawgfx(bitmap,Machine.gfx[0],	skykid_textram.read(offs) + (flipscreen << 8),
						skykid_textram.read(offs+0x400) & 0x3f,
						0,0,sx*8,sy*8,
						Machine.visible_area,TRANSPARENCY_PEN,0);
	        }
		}
		if ((priority & 0xf0) == 0x50)
			skykid_draw_sprites(bitmap);
	} };
}
