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

import static gr.codebb.arcadeflex.WIP.v037b7.drivers.lazercmd.marker_x;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.lazercmd.marker_y;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.TRANSPARENCY_NONE;
import static gr.codebb.arcadeflex.old.mame.drawgfx.drawgfx;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.lazercmdH.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.plot_pixel;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;


public class lazercmd {

    static int video_inverted = 0;

    /*TODO*///	#define JADE	0x20,0xb0,0x20,OVERLAY_DEFAULT_OPACITY
/*TODO*///	#define MUSTARD 0xb0,0x80,0x20,OVERLAY_DEFAULT_OPACITY
    /*TODO*///	#define	END  {{ -1, -1, -1, -1}, 0,0,0,0}
    /*TODO*///	static const struct artwork_element overlay[]=
/*TODO*///	{
/*TODO*///		{{  0*HORZ_CHR, 16*HORZ_CHR-1,  0*VERT_CHR, 1*VERT_CHR-1 }, MUSTARD },
/*TODO*///		{{ 16*HORZ_CHR, 32*HORZ_CHR-1,  0*VERT_CHR, 1*VERT_CHR-1 }, JADE    },
/*TODO*///		{{  0*HORZ_CHR, 16*HORZ_CHR-1,  1*VERT_CHR,22*VERT_CHR-1 }, JADE    },
/*TODO*///		{{ 16*HORZ_CHR, 32*HORZ_CHR-1,  1*VERT_CHR,22*VERT_CHR-1 }, MUSTARD },
/*TODO*///		{{  0*HORZ_CHR, 16*HORZ_CHR-1, 22*VERT_CHR,23*VERT_CHR-1 }, MUSTARD },
/*TODO*///		{{ 16*HORZ_CHR, 32*HORZ_CHR-1, 22*VERT_CHR,23*VERT_CHR-1 }, JADE    },
/*TODO*///		END
/*TODO*///	};
    /* scale a markers vertical position */
 /* the following table shows how the markers */
 /* vertical position worked in hardware  */
 /*  marker_y  lines    marker_y  lines   */
 /*     0      0 + 1       8      10 + 11 */
 /*     1      2 + 3       9      12 + 13 */
 /*     2      4 + 5      10      14 + 15 */
 /*     3      6 + 7      11      16 + 17 */
 /*     4      8 + 9      12      18 + 19 */
    static int vert_scale(int data) {
        return ((data & 0x07) << 1) + ((data & 0xf8) >> 3) * VERT_CHR;
    }

    /* mark the character occupied by the marker dirty */
    public static void lazercmd_marker_dirty(int marker) {
        int x, y;

        x = marker_x - 1;
        /* normal video lags marker by 1 pixel */
        y = vert_scale(marker_y) - VERT_CHR;
        /* first line used as scratch pad */

        if (x < 0 || x >= HORZ_RES * HORZ_CHR) {
            return;
        }

        if (y < 0 || y >= (VERT_RES - 1) * VERT_CHR) {
            return;
        }

        /* mark all occupied character positions dirty */
        dirtybuffer[(y + 0) / VERT_CHR * HORZ_RES + (x + 0) / HORZ_CHR] = 1;
        dirtybuffer[(y + 3) / VERT_CHR * HORZ_RES + (x + 0) / HORZ_CHR] = 1;
        dirtybuffer[(y + 0) / VERT_CHR * HORZ_RES + (x + 3) / HORZ_CHR] = 1;
        dirtybuffer[(y + 3) / VERT_CHR * HORZ_RES + (x + 3) / HORZ_CHR] = 1;
    }

    /* plot a bitmap marker */
 /* hardware has 2 marker sizes 2x2 and 4x2 selected by jumper */
 /* meadows lanes normaly use 2x2 pixels and lazer command uses either */
    static void plot_pattern(osd_bitmap bitmap, int x, int y) {
        int xbit, ybit, size;

        size = 2;
        if ((input_port_2_r.handler(0) & 0x40) != 0) {
            size = 4;
        }

        for (ybit = 0; ybit < 2; ybit++) {
            if (y + ybit < 0 || y + ybit >= VERT_RES * VERT_CHR) {
                return;
            }

            for (xbit = 0; xbit < size; xbit++) {
                if (x + xbit < 0 || x + xbit >= HORZ_RES * HORZ_CHR) {
                    continue;
                }

                plot_pixel.handler(bitmap, x + xbit, y + ybit, Machine.pens[2]);
            }
        }
    }

    public static VhStartPtr lazercmd_vh_start = new VhStartPtr() {
        public int handler() {
            if (generic_vh_start.handler() != 0) {
                return 1;
            }

            /* is overlay enabled? */
 /*TODO*///           if ((input_port_2_r.handler(0) & 0x80) != 0) {
/*TODO*///			overlay_create(overlay, 3, Machine.drv.total_colors-3);
/*TODO*///            }
            return 0;
        }
    };

    public static VhUpdatePtr lazercmd_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int i, x, y;

            if (video_inverted != (input_port_2_r.handler(0) & 0x20)) {
                video_inverted = input_port_2_r.handler(0) & 0x20;
                memset(dirtybuffer, 1, videoram_size[0]);
            }

            if (palette_recalc() != null || full_refresh != 0) {
                memset(dirtybuffer, 1, videoram_size[0]);
            }

            /* The first row of characters are invisible */
            for (i = 0; i < (VERT_RES - 1) * HORZ_RES; i++) {
                if (dirtybuffer[i] != 0) {
                    int sx, sy;

                    dirtybuffer[i] = 0;

                    sx = i % HORZ_RES;
                    sy = i / HORZ_RES;

                    sx *= HORZ_CHR;
                    sy *= VERT_CHR;

                    drawgfx(bitmap, Machine.gfx[0],
                            videoram.read(i), video_inverted != 0 ? 1 : 0,
                            0, 0,
                            sx, sy,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                }
            }

            x = marker_x - 1;
            /* normal video lags marker by 1 pixel */
            y = vert_scale(marker_y) - VERT_CHR;
            /* first line used as scratch pad */
            plot_pattern(bitmap, x, y);
        }
    };
}
