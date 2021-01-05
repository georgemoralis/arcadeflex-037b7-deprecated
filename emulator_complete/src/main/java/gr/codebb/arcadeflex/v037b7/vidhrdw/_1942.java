/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.mame.common.*;
import static gr.codebb.arcadeflex.old.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.old2.mame.common.*;
import static gr.codebb.arcadeflex.old2.mame.mame.Machine;
import static gr.codebb.arcadeflex.old2.mame.tilemapC.*;
import static gr.codebb.arcadeflex.old2.mame.tilemapH.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.v037b7.mame.commonH.flip_screen;
import static gr.codebb.arcadeflex.v037b7.mame.drawgfxH.*;

public class _1942 {

  public static UBytePtr c1942_fgvideoram = new UBytePtr();
  public static UBytePtr c1942_bgvideoram = new UBytePtr();
  public static UBytePtr c1942_spriteram = new UBytePtr();
  public static int[] c1942_spriteram_size = new int[1];

  static int c1942_palette_bank;
  static struct_tilemap fg_tilemap, bg_tilemap;

  /**
   * *************************************************************************
   *
   * <p>Convert the color PROMs into a more useable format.
   *
   * <p>1942 has three 256x4 palette PROMs (one per gun) and three 256x4 lookup table PROMs (one for
   * characters, one for sprites, one for background tiles). The palette PROMs are connected to the
   * RGB output this way:
   *
   * <p>bit 3 -- 220 ohm resistor -- RED/GREEN/BLUE -- 470 ohm resistor -- RED/GREEN/BLUE -- 1 kohm
   * resistor -- RED/GREEN/BLUE bit 0 -- 2.2kohm resistor -- RED/GREEN/BLUE
   *
   * <p>*************************************************************************
   */
  static int TOTAL_COLORS(int gfxn) {
    return Machine.gfx[gfxn].total_colors * Machine.gfx[gfxn].color_granularity;
  }

  public static VhConvertColorPromPtr c1942_vh_convert_color_prom =
      new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
          int p_inc = 0;
          int i;
          // #define TOTAL_COLORS(gfxn) (Machine.gfx[gfxn].total_colors *
          // Machine.gfx[gfxn].color_granularity)
          // #define COLOR(gfxn,offs) (colortable[Machine.drv.gfxdecodeinfo[gfxn].color_codes_start
          // + offs])

          for (i = 0; i < Machine.drv.total_colors; i++) {
            int bit0, bit1, bit2, bit3;

            /* red component */
            bit0 = (color_prom.read(0) >> 0) & 0x01;
            bit1 = (color_prom.read(0) >> 1) & 0x01;
            bit2 = (color_prom.read(0) >> 2) & 0x01;
            bit3 = (color_prom.read(0) >> 3) & 0x01;
            palette[p_inc++] = ((char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3));
            /* green component */
            bit0 = (color_prom.read(Machine.drv.total_colors) >> 0) & 0x01;
            bit1 = (color_prom.read(Machine.drv.total_colors) >> 1) & 0x01;
            bit2 = (color_prom.read(Machine.drv.total_colors) >> 2) & 0x01;
            bit3 = (color_prom.read(Machine.drv.total_colors) >> 3) & 0x01;
            palette[p_inc++] = ((char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3));
            /* blue component */
            bit0 = (color_prom.read(2 * Machine.drv.total_colors) >> 0) & 0x01;
            bit1 = (color_prom.read(2 * Machine.drv.total_colors) >> 1) & 0x01;
            bit2 = (color_prom.read(2 * Machine.drv.total_colors) >> 2) & 0x01;
            bit3 = (color_prom.read(2 * Machine.drv.total_colors) >> 3) & 0x01;
            palette[p_inc++] = ((char) (0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3));

            color_prom.inc();
          }

          color_prom.inc(2 * Machine.drv.total_colors);
          /* color_prom now points to the beginning of the lookup table */

          /* characters use colors 128-143 */
          for (i = 0; i < TOTAL_COLORS(0); i++) {
            colortable[Machine.drv.gfxdecodeinfo[0].color_codes_start + i] =
                (char) ((color_prom.readinc()) + 128);
          }

          /* background tiles use colors 0-63 in four banks */
          for (i = 0; i < TOTAL_COLORS(1) / 4; i++) {
            colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i] =
                (char) (color_prom.read());
            colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i + 32 * 8] =
                (char) (color_prom.read() + 16);
            colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i + 2 * 32 * 8] =
                (char) (color_prom.read() + 32);
            colortable[Machine.drv.gfxdecodeinfo[1].color_codes_start + i + 3 * 32 * 8] =
                (char) (color_prom.read() + 48);
            color_prom.inc();
          }

          /* sprites use colors 64-79 */
          for (i = 0; i < TOTAL_COLORS(2); i++) {
            colortable[Machine.drv.gfxdecodeinfo[2].color_codes_start + i] =
                (char) (color_prom.readinc() + 64);
          }
        }
      };

  /**
   * *************************************************************************
   *
   * <p>Callbacks for the TileMap code
   *
   * <p>*************************************************************************
   */
  public static GetTileInfoPtr get_fg_tile_info =
      new GetTileInfoPtr() {
        @Override
        public void handler(int tile_index) {
          int code, color;

          code = c1942_fgvideoram.read(tile_index);
          color = c1942_fgvideoram.read(tile_index + 0x400);
          SET_TILE_INFO(0, code + ((color & 0x80) << 1), color & 0x3f);
        }
      };

  public static GetTileInfoPtr get_bg_tile_info =
      new GetTileInfoPtr() {
        @Override
        public void handler(int tile_index) {
          int code, color;

          tile_index = (tile_index & 0x0f) | ((tile_index & 0x01f0) << 1);

          code = c1942_bgvideoram.read(tile_index);
          color = c1942_bgvideoram.read(tile_index + 0x10);
          SET_TILE_INFO(
              1, code + ((color & 0x80) << 1), (color & 0x1f) + (0x20 * c1942_palette_bank));
          tile_info.u32_flags = TILE_FLIPYX((color & 0x60) >> 5);
        }
      };

  /**
   * *************************************************************************
   *
   * <p>Start the video hardware emulation.
   *
   * <p>*************************************************************************
   */
  public static VhStartPtr c1942_vh_start =
      new VhStartPtr() {
        public int handler() {
          fg_tilemap =
              tilemap_create(
                  get_fg_tile_info, tilemap_scan_rows, TILEMAP_TRANSPARENT, 8, 8, 32, 32);
          bg_tilemap =
              tilemap_create(get_bg_tile_info, tilemap_scan_cols, TILEMAP_OPAQUE, 16, 16, 32, 16);

          if (fg_tilemap == null || bg_tilemap == null) {
            return 1;
          }

          fg_tilemap.transparent_pen = 0;

          return 0;
        }
      };

  /**
   * *************************************************************************
   *
   * <p>Memory handlers
   *
   * <p>*************************************************************************
   */
  public static WriteHandlerPtr c1942_fgvideoram_w =
      new WriteHandlerPtr() {
        public void handler(int offset, int data) {
          c1942_fgvideoram.write(offset, data);
          tilemap_mark_tile_dirty(fg_tilemap, offset & 0x3ff);
        }
      };

  public static WriteHandlerPtr c1942_bgvideoram_w =
      new WriteHandlerPtr() {
        public void handler(int offset, int data) {
          c1942_bgvideoram.write(offset, data);
          tilemap_mark_tile_dirty(bg_tilemap, (offset & 0x0f) | ((offset >> 1) & 0x01f0));
        }
      };

  public static WriteHandlerPtr c1942_palette_bank_w =
      new WriteHandlerPtr() {
        public void handler(int offset, int data) {
          if (c1942_palette_bank != data) {
            tilemap_mark_all_tiles_dirty(bg_tilemap);
          }

          c1942_palette_bank = data;
        }
      };
  static UBytePtr scroll = new UBytePtr(2);
  public static WriteHandlerPtr c1942_scroll_w =
      new WriteHandlerPtr() {
        public void handler(int offset, int data) {
          scroll.write(offset, data);
          tilemap_set_scrollx(bg_tilemap, 0, scroll.read(0) | (scroll.read(1) << 8));
        }
      };

  public static WriteHandlerPtr c1942_c804_w =
      new WriteHandlerPtr() {
        public void handler(int offset, int data) {
          /* bit 7: flip screen
             bit 4: cpu B reset
          bit 0: coin counter */

          coin_counter_w.handler(offset, data & 0x01);

          cpu_set_reset_line(1, (data & 0x10) != 0 ? ASSERT_LINE : CLEAR_LINE);

          flip_screen_w.handler(offset, data & 0x80);
        }
      };

  /**
   * *************************************************************************
   *
   * <p>Display refresh
   *
   * <p>*************************************************************************
   */
  static void draw_sprites(osd_bitmap bitmap) {
    int offs;

    for (offs = c1942_spriteram_size[0] - 4; offs >= 0; offs -= 4) {
      int i, code, col, sx, sy, dir;

      code =
          (c1942_spriteram.read(offs) & 0x7f)
              + 4 * (c1942_spriteram.read(offs + 1) & 0x20)
              + 2 * (c1942_spriteram.read(offs) & 0x80);
      col = c1942_spriteram.read(offs + 1) & 0x0f;
      sx = c1942_spriteram.read(offs + 3) - 0x10 * (c1942_spriteram.read(offs + 1) & 0x10);
      sy = c1942_spriteram.read(offs + 2);
      dir = 1;
      if (flip_screen() != 0) {
        sx = 240 - sx;
        sy = 240 - sy;
        dir = -1;
      }

      /* handle double / quadruple height */
      i = (c1942_spriteram.read(offs + 1) & 0xc0) >> 6;
      if (i == 2) {
        i = 3;
      }

      do {
        drawgfx(
            bitmap,
            Machine.gfx[2],
            code + i,
            col,
            flip_screen(),
            flip_screen(),
            sx,
            sy + 16 * i * dir,
            Machine.visible_area,
            TRANSPARENCY_PEN,
            15);

        i--;
      } while (i >= 0);
    }
  }

  public static VhUpdatePtr c1942_vh_screenrefresh =
      new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
          tilemap_update(ALL_TILEMAPS);
          tilemap_render(ALL_TILEMAPS);

          tilemap_draw(bitmap, bg_tilemap, 0);
          draw_sprites(bitmap);
          tilemap_draw(bitmap, fg_tilemap, 0);
        }
      };
}
