/***************************************************************************
  vidhrdw/mole.c
  Functions to emulate the video hardware of Mole Attack!.
  Mole Attack's Video hardware is essentially two banks of 512 characters.
  The program uses a single byte to indicate which character goes in each location,
  and uses a control location (0x8400) to select the character sets
***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.TRANSPARENCY_NONE;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import gr.codebb.arcadeflex.common.SubArrays.UShortArray;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;

public class mole
{
	
	static int tile_bank;
	static UShortArray tile_data;
	public static final int NUM_ROWS = 25;
	public static final int NUM_COLS = 40;
	public static final int NUM_TILES = (NUM_ROWS*NUM_COLS);
	
	public static VhConvertColorPromPtr moleattack_vh_convert_color_prom = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) {
		int i;
                int _palette=0;
                
		for( i=0; i<8; i++ ){
			colortable[i] = (char) i;
			palette[_palette++] = (char) ((i&1)!=0?0xff:0x00);
			palette[_palette++] = (char) ((i&4)!=0?0xff:0x00);
			palette[_palette++] = (char) ((i&2)!=0?0xff:0x00);
		}
	} };
	
	public static VhStartPtr moleattack_vh_start = new VhStartPtr() { public int handler() {
		tile_data = new UShortArray( NUM_TILES );
		if (tile_data != null){
			dirtybuffer = new char[ NUM_TILES ];
			if (dirtybuffer != null){
				memset( dirtybuffer, 1, NUM_TILES );
				return 0;
			}
			tile_data = null;
		}
		return 1; /* error */
	} };
	
	public static VhStopPtr moleattack_vh_stop = new VhStopPtr() { public void handler() {
		dirtybuffer = null;
		tile_data = null;
	} };
	
	public static WriteHandlerPtr moleattack_videoram_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		if( offset<NUM_TILES ){
			if( tile_data.read(offset)!=data ){
				dirtybuffer[offset] = 1;
				tile_data.write(offset, data | (tile_bank<<8));
			}
		}
		else if( offset==0x3ff ){ /* hack!  erase screen */
			memset( dirtybuffer, 1, NUM_TILES );
			memset( tile_data, 0, NUM_TILES );
		}
	} };
	
	public static WriteHandlerPtr moleattack_tilesetselector_w = new WriteHandlerPtr() {public void handler(int offset, int data){
		tile_bank = data;
	} };
	
	public static VhUpdatePtr moleattack_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) {
		int offs;
	
		if( full_refresh!=0 || palette_recalc()!=null ){
			memset( dirtybuffer, 1, NUM_TILES );
		}
	
		for( offs=0; offs<NUM_TILES; offs++ ){
			if( dirtybuffer[offs] != 0 ){
				int code = tile_data.read(offs);
				drawgfx( bitmap, Machine.gfx[(code&0x200)!=0?1:0],
					code&0x1ff,
					0, /* color */
					0,0, /* no flip */
					(offs%NUM_COLS)*8, /* xpos */
					(offs/NUM_COLS)*8, /* ypos */
					null, /* no clip */
					TRANSPARENCY_NONE,0 );
	
				dirtybuffer[offs] = 0;
			}
		}
	} };
}
