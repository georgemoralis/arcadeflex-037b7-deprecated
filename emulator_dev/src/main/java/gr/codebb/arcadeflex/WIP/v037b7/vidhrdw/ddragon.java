/***************************************************************************

  Video Hardware for Double Dragon (bootleg) & Double Dragon II

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.tilemapH.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;

public class ddragon
{
	
	
	public static UBytePtr ddragon_bgvideoram=new UBytePtr(),ddragon_fgvideoram=new UBytePtr();
	public static int ddragon_scrollx_hi, ddragon_scrolly_hi;
	public static UBytePtr ddragon_scrollx_lo=new UBytePtr();
	public static UBytePtr ddragon_scrolly_lo=new UBytePtr();
	public static UBytePtr ddragon_spriteram=new UBytePtr();
	public static int dd2_video;
	
	static struct_tilemap fg_tilemap,bg_tilemap;
	
	
	
	/***************************************************************************
	
	  Callbacks for the TileMap code
	
	***************************************************************************/
	
	static GetMemoryOffsetPtr background_scan = new GetMemoryOffsetPtr() {
            public int handler(int col, int row, int num_cols, int num_rows) {
		/* logical (col,row) . memory offset */
		return (col & 0x0f) + ((row & 0x0f) << 4) + ((col & 0x10) << 4) + ((row & 0x10) << 5);
            }
        };
	
	public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int attr = ddragon_bgvideoram.read(2*tile_index);
		SET_TILE_INFO(2,ddragon_bgvideoram.read(2*tile_index+1) + ((attr & 0x07) << 8),(attr >> 3) & 0x07);
		tile_info.u32_flags = TILE_FLIPYX((attr & 0xc0) >> 6);
	} };
	
	public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() { public void handler(int tile_index) 
	{
		int attr = ddragon_fgvideoram.read(2*tile_index);
		SET_TILE_INFO(0,ddragon_fgvideoram.read(2*tile_index+1) + ((attr & 0x07) << 8),attr >> 5);
	} };
	
	
	/***************************************************************************
	
	  Start the video hardware emulation.
	
	***************************************************************************/
	
	public static VhStartPtr ddragon_vh_start = new VhStartPtr() { public int handler() 
	{
		bg_tilemap = tilemap_create(get_bg_tile_info,background_scan,  TILEMAP_OPAQUE,     16,16,32,32);
		fg_tilemap = tilemap_create(get_fg_tile_info,tilemap_scan_rows,TILEMAP_TRANSPARENT, 8, 8,32,32);
	
		if (bg_tilemap==null || fg_tilemap==null)
			return 1;
	
		fg_tilemap.transparent_pen = 0;
	
		return 0;
	} };
	
	
	/***************************************************************************
	
	  Memory handlers
	
	***************************************************************************/
	
	public static WriteHandlerPtr ddragon_bgvideoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (ddragon_bgvideoram.read(offset) != data)
		{
			ddragon_bgvideoram.write(offset, data);
			tilemap_mark_tile_dirty(bg_tilemap,offset/2);
		}
	} };
	
	public static WriteHandlerPtr ddragon_fgvideoram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		if (ddragon_fgvideoram.read(offset) != data)
		{
			ddragon_fgvideoram.write(offset, data);
			tilemap_mark_tile_dirty(fg_tilemap,offset/2);
		}
	} };
	
	
	
	/***************************************************************************
	
	  Display refresh
	
	***************************************************************************/
	
	//#define DRAW_SPRITE( order, sx, sy ) drawgfx( bitmap, gfx, \
	//					(which+order),color,flipx,flipy,sx,sy, \
	//					clip,TRANSPARENCY_PEN,0);
	
	static void draw_sprites(osd_bitmap bitmap)
	{
		rectangle clip = new rectangle(Machine.visible_area);
		GfxElement gfx = Machine.gfx[1];
	
		UBytePtr src = new UBytePtr(ddragon_spriteram, 0x800);
		int i;
	
		for( i = 0; i < ( 64 * 5 ); i += 5 ) {
			int attr = src.read(i+1);
			if ((attr & 0x80) != 0) { /* visible */
				int sx = 240 - src.read(i+4) + ( ( attr & 2 ) << 7 );
				int sy = 240 - src.read(i+0) + ( ( attr & 1 ) << 8 );
				int size = ( attr & 0x30 ) >> 4;
				int flipx = ( attr & 8 );
				int flipy = ( attr & 4 );
				int dx = -16,dy = -16;
	
				int which;
				int color;
	
				if (dd2_video != 0) {
					color = ( src.read(i+2) >> 5 );
					which = src.read(i+3) + ( ( src.read(i+2) & 0x1f ) << 8 );
				} else {
					color = ( src.read(i+2) >> 4 ) & 0x07;
					which = src.read(i+3) + ( ( src.read(i+2) & 0x0f ) << 8 );
				}
	
				if (flip_screen() != 0)
				{
					sx = 240 - sx;
					sy = 240 - sy;
					flipx = flipx!=0?0:1;
					flipy = flipy!=0?0:1;
					dx = -dx;
					dy = -dy;
				}
	
				switch ( size ) {
					case 0: /* normal */
					//DRAW_SPRITE( 0, sx, sy );
                                        drawgfx( bitmap, gfx,
						(which+0),color,flipx,flipy,sx,sy,
						clip,TRANSPARENCY_PEN,0);
					break;
	
					case 1: /* double y */
					//DRAW_SPRITE( 0, sx, sy + dy );
                                        drawgfx( bitmap, gfx,
						(which+0),color,flipx,flipy,sx,sy+dy,
						clip,TRANSPARENCY_PEN,0);
					//DRAW_SPRITE( 1, sx, sy );
                                        drawgfx( bitmap, gfx,
						(which+1),color,flipx,flipy,sx,sy,
						clip,TRANSPARENCY_PEN,0);
					break;
	
					case 2: /* double x */
					//DRAW_SPRITE( 0, sx + dx, sy );
                                        drawgfx( bitmap, gfx,
						(which+0),color,flipx,flipy,sx+dx,sy,
						clip,TRANSPARENCY_PEN,0);
					//DRAW_SPRITE( 2, sx, sy );
                                        drawgfx( bitmap, gfx,
						(which+2),color,flipx,flipy,sx,sy,
						clip,TRANSPARENCY_PEN,0);
					break;
	
					case 3:
					//DRAW_SPRITE( 0, sx + dx, sy + dy );
                                        drawgfx( bitmap, gfx,
						(which+0),color,flipx,flipy,sx+dx,sy+dy,
						clip,TRANSPARENCY_PEN,0);
					//DRAW_SPRITE( 1, sx + dx, sy );
                                        drawgfx( bitmap, gfx,
						(which+1),color,flipx,flipy,sx+dx,sy,
						clip,TRANSPARENCY_PEN,0);
					//DRAW_SPRITE( 2, sx, sy + dy );
                                        drawgfx( bitmap, gfx,
						(which+2),color,flipx,flipy,sx,sy+dy,
						clip,TRANSPARENCY_PEN,0);
					//DRAW_SPRITE( 3, sx, sy );
                                        drawgfx( bitmap, gfx,
						(which+3),color,flipx,flipy,sx,sy,
						clip,TRANSPARENCY_PEN,0);
					break;
				}
			}
		}
	}
	
	//#undef DRAW_SPRITE
	
	
	public static VhUpdatePtr ddragon_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int scrollx = ddragon_scrollx_hi + ddragon_scrollx_lo.read();
		int scrolly = ddragon_scrolly_hi + ddragon_scrolly_lo.read();
	
		tilemap_set_scrollx(bg_tilemap,0,scrollx);
		tilemap_set_scrolly(bg_tilemap,0,scrolly);
	
		tilemap_update(ALL_TILEMAPS);
	
		if (palette_recalc() != null)
			tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
	
		tilemap_render(ALL_TILEMAPS);
	
		tilemap_draw(bitmap,bg_tilemap,0);
		draw_sprites(bitmap);
		tilemap_draw(bitmap,fg_tilemap,0);
	} };
}
