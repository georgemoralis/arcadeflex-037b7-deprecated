/*
	Video Hardware for Shoot Out
	prom GB09.K6 may be related to background tile-sprite priority
*/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.spriteC.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.spriteH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.old.mame.drawgfx.copybitmap;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class shootout
{
	
	public static final int NUM_SPRITES = 128;
	
	static sprite_list sprite_list;
	public static UBytePtr shootout_textram=new UBytePtr();
	
	public static VhStartPtr shootout_vh_start = new VhStartPtr() { public int handler() {
		if( generic_vh_start.handler()==0 ){
			sprite_list = sprite_list_create( NUM_SPRITES, SPRITE_LIST_BACK_TO_FRONT );
			if (sprite_list != null){
				int i;
				sprite_list.sprite_type = SPRITE_TYPE_STACK;
	
				for( i=0; i<NUM_SPRITES; i++ ){
					sprite sprite = sprite_list.sprite[i];
					sprite.pal_data = Machine.gfx[1].colortable;
					sprite.tile_width = 16;
					sprite.tile_height = 16;
					sprite.total_width = 16;
					sprite.line_offset = 16;
				}
				sprite_list.max_priority = 1;
	
				return 0;
			}
			generic_vh_stop.handler();
		}
		return 1; /* error */
	} };
	
	static void get_sprite_info(){
		GfxElement gfx = Machine.gfx[1];
		UBytePtr source = new UBytePtr(spriteram);
		sprite[] sprite = sprite_list.sprite;
		int count = NUM_SPRITES;
                int sprite_ptr=0;
	
		int attributes, flags, number;
	
		while( count-- != 0){
			flags = 0;
			attributes = source.read(1);
			/*
			    76543210
				xxx			bank
				   x		vertical size
				    x		priority
				     x		horizontal flip
				      x		flicker
				       x	enable
			*/
			if ((attributes & 0x01) != 0){ /* enabled */
				flags |= SPRITE_VISIBLE;
				sprite[sprite_ptr].priority = (attributes&0x08)!=0?1:0;
				sprite[sprite_ptr].x = (240 - source.read(2))&0xff;
				sprite[sprite_ptr].y = (240 - source.read(0))&0xff;
	
				number = source.read(3) + ((attributes&0xe0)<<3);
				if ((attributes & 0x04) != 0) flags |= SPRITE_FLIPX;
				if ((attributes & 0x02) != 0) flags |= SPRITE_FLICKER; /* ? */
	
				if ((attributes & 0x10) != 0){ /* double height */
					number = number&(~1);
					sprite[sprite_ptr].y -= 16;
					sprite[sprite_ptr].total_height = 32;
				}
				else {
					sprite[sprite_ptr].total_height = 16;
				}
				sprite[sprite_ptr].pen_data = new UBytePtr(gfx.gfxdata, number * gfx.char_modulo);
			}
			sprite[sprite_ptr].flags = flags;
			sprite_ptr++;
			source.inc( 4 );
		}
	}
	
	static void get_sprite_info2(){
		GfxElement gfx = Machine.gfx[1];
		UBytePtr source = new UBytePtr(spriteram);
		sprite[] sprite = sprite_list.sprite;
		int count = NUM_SPRITES;
                int sprite_ptr=0;
	
		int attributes, flags, number;
	
		while( count-- != 0){
			flags = 0;
			attributes = source.read(1);
			if ((attributes & 0x01) != 0){ /* enabled */
				flags |= SPRITE_VISIBLE;
				sprite[sprite_ptr].priority = (attributes&0x08)!=0?1:0;
				sprite[sprite_ptr].x = (240 - source.read(2))&0xff;
				sprite[sprite_ptr].y = (240 - source.read(0))&0xff;
	
				number = source.read(3) + ((attributes&0xc0)<<2);
				if ((attributes & 0x04) != 0) flags |= SPRITE_FLIPX;
				if ((attributes & 0x02) != 0) flags |= SPRITE_FLICKER; /* ? */
	
				if ((attributes & 0x10) != 0){ /* double height */
					number = number&(~1);
					sprite[sprite_ptr].y -= 16;
					sprite[sprite_ptr].total_height = 32;
				}
				else {
					sprite[sprite_ptr].total_height = 16;
				}
				sprite[sprite_ptr].pen_data = new UBytePtr(gfx.gfxdata, number * gfx.char_modulo);
			}
			sprite[sprite_ptr].flags = flags;
			sprite_ptr++;
			source.inc( 4 );
		}
	}
	
	static void draw_background( osd_bitmap bitmap ){
		rectangle clip = new rectangle(Machine.visible_area);
		int offs;
		for( offs=0; offs<videoram_size[0]; offs++ ){
			if( dirtybuffer[offs] != 0 ){
				int sx = (offs%32)*8;
				int sy = (offs/32)*8;
				int attributes = colorram.read(offs); /* CCCC -TTT */
				int tile_number = videoram.read(offs)+ 256*(attributes&7);
				int color = attributes>>4;
	
				drawgfx(tmpbitmap,Machine.gfx[2],
						tile_number&0x7ff,
						color,
						0,0,
						sx,sy,
						clip,TRANSPARENCY_NONE,0);
	
				dirtybuffer[offs] = 0;
			}
		}
		copybitmap(bitmap,tmpbitmap,0,0,0,0,Machine.visible_area,TRANSPARENCY_NONE,0);
	}
	
	static void draw_foreground( osd_bitmap bitmap ){
		rectangle clip = new rectangle(Machine.visible_area);
		GfxElement gfx = Machine.gfx[0];
		int sx,sy;
	
		UBytePtr source = new UBytePtr(shootout_textram);
	
		for( sy=0; sy<256; sy+=8 ){
			for( sx=0; sx<256; sx+=8 ){
				int attributes = source.read(videoram_size[0]);//*(source+videoram_size); /* CCCC --TT */
				int tile_number = 256*(attributes&0x3) + source.readinc();
				int color = attributes>>4;
				drawgfx(bitmap,gfx,
					tile_number, /* 0..1024 */
					color,
					0,0,
					sx,sy,
					clip,TRANSPARENCY_PEN,0);
			}
		}
	}
	
	public static VhUpdatePtr shootout_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) {
		get_sprite_info();
		sprite_update();
		draw_background( bitmap );
		sprite_draw( sprite_list, 1);
		draw_foreground( bitmap );
		sprite_draw( sprite_list, 0);
	} };
	
	public static VhUpdatePtr shootouj_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) {
		get_sprite_info2();
		sprite_update();
		draw_background( bitmap );
		sprite_draw( sprite_list, 1);
		draw_foreground( bitmap );
		sprite_draw( sprite_list, 0);
	} };
}
