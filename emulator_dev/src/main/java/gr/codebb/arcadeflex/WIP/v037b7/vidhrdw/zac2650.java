/*
This file is part of Arcadeflex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */
 /*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.artworkC.overlay_create;
import gr.codebb.arcadeflex.WIP.v037b7.mame.artworkH.artwork_element;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.decodechar;

public class zac2650 {

    public static UBytePtr s2636ram = new UBytePtr();
    public static osd_bitmap spritebitmap;

    public static int[] dirtychar = new int[26];
    public static int CollisionBackground;
    public static int CollisionSprite;

    static artwork_element tinv2650_overlay[] = new artwork_element[]{
        new artwork_element(new rectangle(0, 255, 0, 255), 0xff, 0xff, 0xff, 0xff),
        new artwork_element(new rectangle(16, 71, 0, 255), 0x20, 0xff, 0x20, 0xff),
        new artwork_element(new rectangle(0, 15, 48, 133), 0x20, 0xff, 0x20, 0xff),
        new artwork_element(new rectangle(192, 208, 0, 255), 0xff, 0x20, 0xff, 0xff),
        new artwork_element(new rectangle(-1, -1, -1, -1), 0, 0, 0, 0)
    };
    /**
     * ***********************************************************
     */
    /* The S2636 is a standard sprite chip used by several boards */
 /* Emulation of this chip may be moved into a seperate unit   */
 /* once it's workings are fully understood.                   */
    /**
     * ***********************************************************
     */
    public static WriteHandlerPtr s2636_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            if (s2636ram.read(offset) != data) {
                s2636ram.write(offset, data);
                dirtychar[offset >> 3] = 1;
            }
        }
    };

    public static ReadHandlerPtr s2636_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            if (offset != 0xCB) {
                return s2636ram.read(offset);
            } else {
                return CollisionSprite;
            }
        }
    };

    public static ReadHandlerPtr tinvader_port_0_r = new ReadHandlerPtr() {
        public int handler(int offset) {
            return input_port_0_r.handler(0) - CollisionBackground;
        }
    };

    /**
     * *************************************************
     */
    /* Convert from 6x8 based co-ordinates to 8x8 based */
    /**
     * *************************************************
     */
    static int remap_x(int x) {
        return ((x * 8) / 6) - 10;
    }

    /**
     * **************************************
     */
    /* Check for Collision between 2 sprites */
    /**
     * **************************************
     */
    public static int SpriteCollision(int first, int second) {
        int Checksum = 0;
        int x, y;

        if ((s2636ram.read(first * 0x10 + 10) < 0xf0) && (s2636ram.read(second * 0x10 + 10) < 0xf0)) {
            int fx = remap_x(s2636ram.read(first * 0x10 + 10));
            int fy = s2636ram.read(first * 0x10 + 12) + 3;
            int expand = (first == 1) ? 2 : 1;

            /* Draw first sprite */
            drawgfx(spritebitmap, Machine.gfx[expand],
                    first * 2,
                    0,
                    0, 0,
                    fx, fy,
                    null, TRANSPARENCY_NONE, 0);

            /* Get fingerprint */
            for (x = fx; x < fx + Machine.gfx[expand].width; x++) {
                for (y = fy; y < fy + Machine.gfx[expand].height; y++) {
                    if ((x < Machine.visible_area.min_x)
                            || (x > Machine.visible_area.max_x)
                            || (y < Machine.visible_area.min_y)
                            || (y > Machine.visible_area.max_y)) {
                        continue;
                    }

                    Checksum += read_pixel.handler(spritebitmap, x, y);
                }
            }

            /* Blackout second sprite */
            drawgfx(spritebitmap, Machine.gfx[1],
                    second * 2,
                    1,
                    0, 0,
                    remap_x(s2636ram.read(second * 0x10 + 10)), s2636ram.read(second * 0x10 + 12) + 3,
                    null, TRANSPARENCY_PEN, 0);

            /* Remove fingerprint */
            for (x = fx; x < fx + Machine.gfx[expand].width; x++) {
                for (y = fy; y < fy + Machine.gfx[expand].height; y++) {
                    if ((x < Machine.visible_area.min_x)
                            || (x > Machine.visible_area.max_x)
                            || (y < Machine.visible_area.min_y)
                            || (y > Machine.visible_area.max_y)) {
                        continue;
                    }

                    Checksum -= read_pixel.handler(spritebitmap, x, y);
                }
            }

            /* Zero bitmap */
            drawgfx(spritebitmap, Machine.gfx[expand],
                    first * 2,
                    1,
                    0, 0,
                    fx, fy,
                    null, TRANSPARENCY_NONE, 0);
        }

        return Checksum;
    }

    public static VhStartPtr tinvader_vh_start = new VhStartPtr() {
        public int handler() {
            overlay_create(tinv2650_overlay, 1, 4);

            generic_vh_start.handler();

            if ((spritebitmap = bitmap_alloc(Machine.drv.screen_width, Machine.drv.screen_height)) == null) {
                bitmap_free(tmpbitmap);
                dirtybuffer = null;
                return 1;
            }

            return 0;
        }
    };

    public static VhStopPtr tinvader_vh_stop = new VhStopPtr() {
        public void handler() {
            generic_vh_stop.handler();
            bitmap_free(spritebitmap);
            spritebitmap = null;
        }
    };

    public static VhUpdatePtr tinvader_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            /* for every character in the Video RAM, check if it has been modified */
            for (offs = videoram_size[0] - 1; offs >= 0; offs--) {
                if (dirtybuffer[offs] != 0) {
                    int sx, sy;

                    dirtybuffer[offs] = 0;

                    sx = offs % 32;
                    sy = offs / 32;

                    drawgfx(tmpbitmap, Machine.gfx[0],
                            videoram.read(offs),
                            0,
                            0, 0,
                            8 * sx, 8 * sy,
                            null, TRANSPARENCY_NONE, 0);
                }
            }

            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, Machine.visible_area, TRANSPARENCY_NONE, 0);

            /* -------------------------------------------------------------- */
 /* There seems to be a strange setup with this board, in that it  */
 /* appears that the S2636 runs from a different clock than the    */
 /* background generator, When the program maps sprite position to */
 /* character position it only has 6 pixels of sprite for 8 pixels */
 /* of character.                                                  */
 /* -------------------------------------------------------------- */
 /* n.b. The original has several graphic glitches as well, so it  */
 /* does not seem to be a fault of the emulation!                  */
 /* -------------------------------------------------------------- */
            CollisionBackground = 0;
            /* Read from 0x1e80 bit 7 */

            for (offs = 0; offs < 0x50; offs += 0x10) {
                if ((s2636ram.read(offs + 10) < 0xF0) && (offs != 0x30)) {
                    int spriteno = (offs / 8);
                    int expand = ((s2636ram.read(0xc0) & (spriteno * 2)) != 0) ? 2 : 1;
                    int bx = remap_x(s2636ram.read(offs + 10));
                    int by = s2636ram.read(offs + 12) + 3;
                    int x, y;

                    if (dirtychar[spriteno] != 0) {
                        /* 16x8 version */
                        decodechar(Machine.gfx[1], spriteno, s2636ram, Machine.drv.gfxdecodeinfo[1].gfxlayout);

                        /* 16x16 version */
                        decodechar(Machine.gfx[2], spriteno, s2636ram, Machine.drv.gfxdecodeinfo[2].gfxlayout);

                        dirtychar[spriteno] = 0;
                    }

                    /* Sprite.Background collision detection */
                    drawgfx(bitmap, Machine.gfx[expand],
                            spriteno,
                            1,
                            0, 0,
                            bx, by,
                            null, TRANSPARENCY_PEN, 0);

                    for (x = bx; x < bx + Machine.gfx[expand].width; x++) {
                        for (y = by; y < by + Machine.gfx[expand].height; y++) {
                            if ((x < Machine.visible_area.min_x)
                                    || (x > Machine.visible_area.max_x)
                                    || (y < Machine.visible_area.min_y)
                                    || (y > Machine.visible_area.max_y)) {
                                continue;
                            }

                            if (read_pixel.handler(bitmap, x, y) != read_pixel.handler(tmpbitmap, x, y)) {
                                CollisionBackground = 0x80;
                                break;
                            }
                        }
                    }

                    drawgfx(bitmap, Machine.gfx[expand],
                            spriteno,
                            0,
                            0, 0,
                            bx, by,
                            null, TRANSPARENCY_PEN, 0);
                }
            }

            /* Sprite.Sprite collision detection */
            CollisionSprite = 0;
            //  if(SpriteCollision(0,1)) CollisionSprite |= 0x20;	/* Not Used */
            if (SpriteCollision(0, 2) != 0) {
                CollisionSprite |= 0x10;
            }
            if (SpriteCollision(0, 4) != 0) {
                CollisionSprite |= 0x08;
            }
            if (SpriteCollision(1, 2) != 0) {
                CollisionSprite |= 0x04;
            }
            if (SpriteCollision(1, 4) != 0) {
                CollisionSprite |= 0x02;
            }
            //  if(SpriteCollision(2,4)) CollisionSprite |= 0x01;	/* Not Used */
        }
    };
}
