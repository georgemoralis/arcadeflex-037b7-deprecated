/*
 * ported to v0.37b7
 */
package gr.codebb.arcadeflex.WIP.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.machine.playch10.*;
import static gr.codebb.arcadeflex.common.libc.cstring.memset;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrfH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptport.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.ppu2c03b.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.ppu2c03bH.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.*;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;

public class playch10 {


    public static VhConvertColorPromPtr playch10_vh_convert_color_prom = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int i;
            int p_ptr = 0;
            for (i = 0; i < 256; i++) {
                int bit0, bit1, bit2, bit3;

			/* red component */
                bit0 = ~(color_prom.read(0) >> 0) & 0x01;
                bit1 = ~(color_prom.read(0) >> 1) & 0x01;
                bit2 = ~(color_prom.read(0) >> 2) & 0x01;
                bit3 = ~(color_prom.read(0) >> 3) & 0x01;
                palette[p_ptr++]=(char)(( 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3)&0xFF);
			/* green component */
                bit0 = ~(color_prom.read(256) >> 0) & 0x01;
                bit1 = ~(color_prom.read(256) >> 1) & 0x01;
                bit2 = ~(color_prom.read(256) >> 2) & 0x01;
                bit3 = ~(color_prom.read(256) >> 3) & 0x01;
                palette[p_ptr++]=(char)(( 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3)&0xFF);
			/* blue component */
                bit0 = ~(color_prom.read(2 * 256) >> 0) & 0x01;
                bit1 = ~(color_prom.read(2 * 256) >> 1) & 0x01;
                bit2 = ~(color_prom.read(2 * 256) >> 2) & 0x01;
                bit3 = ~(color_prom.read(2 * 256) >> 3) & 0x01;
                palette[p_ptr++]=(char)(( 0x0e * bit0 + 0x1f * bit1 + 0x43 * bit2 + 0x8f * bit3)&0xFF);

                color_prom.inc();

                colortable[i] = (char) i;
            }

            ppu2c03b_init_palette(p_ptr,palette);
        }
    };
    public static ppu2c03b_irq_cb ppu_irq = new ppu2c03b_irq_cb() {
        public void handler(int num) {
            cpu_set_nmi_line(1, PULSE_LINE);
            pc10_int_detect = 1;
        }
    };

    /* our ppu interface											*/
	/* things like mirroring and wether to use vrom or vram			*/
	/* can be set by calling 'ppu2c03b_override_hardware_options'	*/
    static ppu2c03b_interface ppu_interface = new ppu2c03b_interface
            (
                    1,						/* num */
                    new int[]{REGION_GFX2},		/* vrom gfx region */
                    new int[]{1},					/* gfxlayout num */
                    new int[]{256},				/* color base */
                    new int[]{PPU_MIRROR_NONE},	/* mirroring */
                    new ppu2c03b_irq_cb[]{ppu_irq}				/* irq */
            );

    public static VhStartPtr playch10_vh_start = new VhStartPtr() {
        public int handler() {
            if (ppu2c03b_init(ppu_interface) != 0)
                return 1;
	
		/* the bios uses the generic stuff */
            return generic_vh_start.handler();
        }
    };

    public static VhStopPtr playch10_vh_stop = new VhStopPtr() {
        public void handler() {
            ppu2c03b_dispose();
            generic_vh_stop.handler();
        }
    };


    /***************************************************************************
     * Display refresh
     ***************************************************************************/

    public static VhUpdatePtr playch10_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int offs;

            rectangle top_monitor = Machine.visible_area;
            rectangle bottom_monitor = Machine.visible_area;

            top_monitor.max_y = (top_monitor.max_y - top_monitor.min_y) / 2;
            bottom_monitor.min_y = (bottom_monitor.max_y - bottom_monitor.min_y) / 2;

            if (palette_recalc() != null || full_refresh != 0)
                memset(dirtybuffer, 1, videoram_size[0]);
	
		/* On Playchoice 10 single monitor, this bit toggles	*/
		/* between PPU and BIOS display.						*/
		/* We support the multi-monitor layout. In this case,	*/
		/* if the bit is not set, then we should display		*/
		/* the PPU portion.										*/

            if (pc10_dispmask == 0) {
			/* render the ppu */
                ppu2c03b_render(0, bitmap, 0, 0, 0, 30 * 8);
	
			/* if this is a gun game, draw a simple crosshair */
                if (pc10_gun_controller != 0) {
                    int x_center = readinputport(5);
                    int y_center = readinputport(6) + 30 * 8;
                    char color;
                    int x, y;
                    int[] minmax_x = new int[2];
                    int[] minmax_y = new int[2];

                    minmax_x[0] = Machine.visible_area.min_x;
                    minmax_x[1] = Machine.visible_area.max_x;
                    minmax_y[0] = Machine.visible_area.min_y + 30 * 8;
                    minmax_y[1] = Machine.visible_area.max_y;

                    color = Machine.pens[1]; /* white */

                    if (x_center < (minmax_x[0] + 2))
                        x_center = minmax_x[0] + 2;

                    if (x_center > (minmax_x[1] - 2))
                        x_center = minmax_x[1] - 2;

                    if (y_center < (minmax_y[0] + 2))
                        y_center = minmax_y[0] + 2;

                    if (y_center > (minmax_y[1] - 2))
                        y_center = minmax_y[1] - 2;

                    for (y = y_center - 5; y < y_center + 6; y++) {
                        if ((y >= minmax_y[0]) && (y <= minmax_y[1]))
                            plot_pixel.handler(bitmap, x_center, y, color);
                    }

                    for (x = x_center - 5; x < x_center + 6; x++) {
                        if ((x >= minmax_x[0]) && (x <= minmax_x[1]))
                            plot_pixel.handler(bitmap, x, y_center, color);
                    }
                }
            } else {
			/* the ppu is masked, clear out the area */
                fillbitmap(bitmap, Machine.pens[0], bottom_monitor);
            }
	
		/* When the bios is accessing vram, the video circuitry cant access it */
            if (pc10_sdcs != 0) {
                fillbitmap(bitmap, Machine.pens[0], top_monitor);
                return;
            }

            for (offs = videoram_size[0] - 2; offs >= 0; offs -= 2) {
                if (dirtybuffer[offs] != 0 || dirtybuffer[offs + 1] != 0) {
                    int offs2 = offs / 2;

                    int sx = offs2 % 32;
                    int sy = offs2 / 32;

                    int tilenum = videoram.read(offs) + ((videoram.read(offs + 1) & 7) << 8);
                    int color = (videoram.read(offs + 1) >> 3) & 0x1f;

                    dirtybuffer[offs] = dirtybuffer[offs + 1] = 0;

                    drawgfx(tmpbitmap, Machine.gfx[0],
                            tilenum,
                            color,
                            0, 0,
                            8 * sx, 8 * sy,
                            Machine.visible_area, TRANSPARENCY_NONE, 0);
                }
            }
	
		/* copy the temporary bitmap to the screen */
            copybitmap(bitmap, tmpbitmap, 0, 0, 0, 0, top_monitor, TRANSPARENCY_NONE, 0);
        }
    };
}
