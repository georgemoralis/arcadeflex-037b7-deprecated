/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.common.libc.cstring.*;
import static gr.codebb.arcadeflex.common.libc.expressions.*;
import static gr.codebb.arcadeflex.mame.osdependH.*;
import static gr.codebb.arcadeflex.mame.palette.*;
import static gr.codebb.arcadeflex.mame.paletteH.*;
import static gr.codebb.arcadeflex.old.arcadeflex.osdepend.*;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old2.mame.mame.Machine;
import static gr.codebb.arcadeflex.old2.mame.tilemapC.*;
import static gr.codebb.arcadeflex.old2.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.drivers.airbustr.*;
import static gr.codebb.arcadeflex.re.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.vidhrdw.generic.*;

public class airbustr {

    static struct_tilemap bg_tilemap, fg_tilemap;

    /* Variables that drivers has access to */
    public static UBytePtr airbustr_bgram = new UBytePtr();
    public static UBytePtr airbustr_fgram = new UBytePtr();

    /**
     * *************************************************************************
     *
     * Callbacks for the TileMap code
     *
     **************************************************************************
     */
    public static GetTileInfoPtr get_fg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = airbustr_fgram.read(tile_index + 0x400);
            SET_TILE_INFO(0,
                    airbustr_fgram.read(tile_index) + ((attr & 0x0f) << 8),
                    (attr >> 4) + 0);
        }
    };

    public static GetTileInfoPtr get_bg_tile_info = new GetTileInfoPtr() {
        public void handler(int tile_index) {
            /*unsigned*/ char attr = airbustr_bgram.read(tile_index + 0x400);
            SET_TILE_INFO(0,
                    airbustr_bgram.read(tile_index) + ((attr & 0x0f) << 8),
                    (attr >> 4) + 16);
        }
    };

    public static VhStartPtr airbustr_vh_start = new VhStartPtr() {
        public int handler() {
            fg_tilemap = tilemap_create(get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 16, 16, 32, 32);
            bg_tilemap = tilemap_create(get_bg_tile_info, tilemap_scan_rows, TILEMAP_OPAQUE, 16, 16, 32, 32);

            if (fg_tilemap == null || bg_tilemap == null) {
                return 1;
            }

            fg_tilemap.transparent_pen = 0;

            return 0;
        }
    };

    public static WriteHandlerPtr airbustr_fgram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (airbustr_fgram.read(offset) != data) {
                airbustr_fgram.write(offset, data);
                tilemap_mark_tile_dirty(fg_tilemap, offset & 0x3ff);
            }
        }
    };

    public static WriteHandlerPtr airbustr_bgram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (airbustr_bgram.read(offset) != data) {
                airbustr_bgram.write(offset, data);
                tilemap_mark_tile_dirty(bg_tilemap, offset & 0x3ff);
            }
        }
    };

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
    public static WriteHandlerPtr airbustr_scrollregs_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            int xoffs, yoffs;

            if (flipscreen != 0) {
                xoffs = -0x06a;
                yoffs = -0x1ff;
            } else {
                xoffs = -0x094;
                yoffs = -0x100;
            }

            switch (offset) // offset 0 <. port 4
            {
                case 0x00:
                    fg_scrolly = data;
                    break;	// low 8 bits
                case 0x02:
                    fg_scrollx = data;
                    break;
                case 0x04:
                    bg_scrolly = data;
                    break;
                case 0x06:
                    bg_scrollx = data;
                    break;
                case 0x08:
                    highbits = ~data;
                    break;	// complemented high bits

                default:
                    logerror("CPU #2 - port %02X written with %02X - PC = %04X\n", offset, data, cpu_get_pc());
            }

            tilemap_set_scrollx(bg_tilemap, 0, ((highbits << 6) & 0x100) + bg_scrollx + xoffs);
            tilemap_set_scrolly(bg_tilemap, 0, ((highbits << 5) & 0x100) + bg_scrolly + yoffs);
            tilemap_set_scrollx(fg_tilemap, 0, ((highbits << 8) & 0x100) + fg_scrollx + xoffs);
            tilemap_set_scrolly(fg_tilemap, 0, ((highbits << 7) & 0x100) + fg_scrolly + yoffs);
        }
    };

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
    static void draw_sprites(osd_bitmap bitmap) {
        int i, offs;

        /* Let's draw the sprites */
        for (i = 0; i < 2; i++) {
            UBytePtr ram = new UBytePtr(spriteram, i * 0x800);
            int sx = 0;
            int sy = 0;

            for (offs = 0; offs < 0x100; offs++) {
                int attr = ram.read(offs + 0x300);
                int x = ram.read(offs + 0x400) - ((attr << 8) & 0x100);
                int y = ram.read(offs + 0x500) - ((attr << 7) & 0x100);

                int gfx = ram.read(offs + 0x700);
                int code = ram.read(offs + 0x600) + ((gfx & 0x1f) << 8);
                int flipx = gfx & 0x80;
                int flipy = gfx & 0x40;

                /* multi sprite */
                if ((attr & 0x04) != 0) {
                    sx += x;
                    sy += y;
                } else {
                    sx = x;
                    sy = y;
                }

                if (flipscreen != 0) {
                    sx = 240 - sx;
                    sy = 240 - sy;
                    flipx = NOT(flipx);
                    flipy = NOT(flipy);
                }

                drawgfx(bitmap, Machine.gfx[1],
                        code,
                        attr >> 4,
                        flipx, flipy,
                        sx, sy,
                        Machine.visible_area, TRANSPARENCY_PEN, 0);

                /* let's get back to normal to support multi sprites */
                if (flipscreen != 0) {
                    sx = 240 - sx;
                    sy = 240 - sy;
                }

            }
        }

    }

    public static VhUpdatePtr airbustr_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            UBytePtr ram;
            int i, offs;

            tilemap_update(ALL_TILEMAPS);

            /* Palette Stuff */
            palette_init_used_colors();

            /* Sprites */
            for (i = 0; i < 2; i++) {
                ram = new UBytePtr(spriteram, i * 0x800 + 0x300);	// color code
                for (offs = 0; offs < 0x100; offs++) {
                    int color = 256 * 2 + (ram.read(offs) & 0xf0);
                    memset(palette_used_colors, color + 1, PALETTE_COLOR_USED, 16 - 1);
                }
            }

            if (palette_recalc() != null) {
                tilemap_mark_all_pixels_dirty(ALL_TILEMAPS);
            }

            tilemap_render(ALL_TILEMAPS);

            tilemap_draw(bitmap, bg_tilemap, 0);
            tilemap_draw(bitmap, fg_tilemap, 0);
            draw_sprites(bitmap);
        }
    };
}
