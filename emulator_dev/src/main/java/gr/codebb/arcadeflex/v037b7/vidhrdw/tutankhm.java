 /*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.v037b7.vidhrdw;

import static gr.codebb.arcadeflex.WIP.v037b7.mame.commonH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.Machine;
import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.generic.*;
import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;
import static gr.codebb.arcadeflex.old.mame.common.memory_region;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.flip_screen_x;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.common.flip_screen_y;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfx.copyscrollbitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.palette.palette_recalc;
import static gr.codebb.arcadeflex.old.mame.drawgfx.*;

public class tutankhm {

    public static UBytePtr tutankhm_scrollx = new UBytePtr();
    public static WriteHandlerPtr videowrite = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /*unsigned char*/
            int x1, x2, y1, y2;

            x1 = (offset & 0x7f) << 1;
            y1 = (offset >> 7);
            x2 = x1 + 1;
            y2 = y1;

            if (flip_screen_x[0] != 0) {
                x1 = 255 - x1;
                x2 = 255 - x2;
            }
            if (flip_screen_y[0] != 0) {
                y1 = 255 - y1;
                y2 = 255 - y2;
            }

            plot_pixel.handler(tmpbitmap, x1, y1, Machine.pens[data & 0x0f]);
            plot_pixel.handler(tmpbitmap, x2, y2, Machine.pens[data >> 4]);
        }
    };

    public static WriteHandlerPtr tutankhm_videoram_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            videoram.write(offset, data);
            videowrite.handler(offset, data);
        }
    };

    /**
     * *************************************************************************
     *
     * Draw the game screen in the given osd_bitmap. Do NOT call
     * osd_update_display() from this function, it will be called by the main
     * emulation engine.
     *
     **************************************************************************
     */
    public static VhUpdatePtr tutankhm_vh_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            if (palette_recalc() != null || full_refresh != 0) {
                int offs;

                for (offs = 0; offs < videoram_size[0]; offs++) {
                    tutankhm_videoram_w.handler(offs, videoram.read(offs));
                }
            }

            /* copy the temporary bitmap to the screen */
            {
                int[] scroll = new int[32];
                int i;

                if (flip_screen_x[0] != 0) {
                    for (i = 0; i < 8; i++) {
                        scroll[i] = 0;
                    }
                    for (i = 8; i < 32; i++) {
                        scroll[i] = -tutankhm_scrollx.read();
                        if (flip_screen_y[0] != 0) {
                            scroll[i] = -scroll[i];
                        }
                    }
                } else {
                    for (i = 0; i < 24; i++) {
                        scroll[i] = -tutankhm_scrollx.read();
                        if (flip_screen_y[0] != 0) {
                            scroll[i] = -scroll[i];
                        }
                    }
                    for (i = 24; i < 32; i++) {
                        scroll[i] = 0;
                    }
                }

                copyscrollbitmap(bitmap, tmpbitmap, 0, null, 32, scroll, Machine.visible_area, TRANSPARENCY_NONE, 0);
            }
        }
    };

    /* Juno First Blitter Hardware emulation
	
		Juno First can blit a 16x16 graphics which comes from un-memory mapped graphics roms
	
		$8070.$8071 specifies the destination NIBBLE address
		$8072.$8073 specifies the source NIBBLE address
	
		Depending on bit 0 of the source address either the source pixels will be copied to
		the destination address, or a zero will be written.
		This allows the game to quickly clear the sprites from the screen
	
		A lookup table is used to swap the source nibbles as they are the wrong way round in the
		source data.
	
		Bugs -
	
			Currently only the even pixels will be written to. This is to speed up the blit routine
			as it does not have to worry about shifting the source data.
			This means that all destination X values will be rounded to even values.
			In practice no one actaully notices this.
	
			The clear works properly.
     */
    static UBytePtr blitterdata = new UBytePtr(4);
    public static WriteHandlerPtr junofrst_blitter_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {

            blitterdata.write(offset, data);
            /* Blitter is triggered by $8073 */
            if (offset == 3) {
                int i;
                /*unsigned*/ char srcaddress;
                /*unsigned*/ char destaddress;
                /*unsigned*/ char srcflag;
                /*unsigned*/ char destflag;
                UBytePtr JunoBLTRom = memory_region(REGION_GFX1);

                srcaddress = (char) (((blitterdata.read(0x2) << 8) | (blitterdata.read(0x3)))&0xFFFF);
                srcflag = (char) ((srcaddress & 1) & 0xFF);
                srcaddress =  (char)((srcaddress >>>1)&0xFFFF);
                srcaddress &= 0x7FFE;
                destaddress = (char) (((blitterdata.read(0x0) << 8) | (blitterdata.read(0x1)))&0xFFFF);

                destflag = (char) ((destaddress & 1) & 0xFF);

                destaddress =(char)((destaddress >>>1)&0xFFFF);
                destaddress &= 0x7fff;

                if (srcflag != 0) {
                    for (i = 0; i < 16; i++) {
                        for (int x = 0; x <= 7; x++) {
                            if (JunoBLTRom.read(srcaddress + x) != 0) {
                                tutankhm_videoram_w.handler(destaddress + x,
                                        ((JunoBLTRom.read(srcaddress + x) & 0xf0) >> 4)
                                        | ((JunoBLTRom.read(srcaddress + x) & 0x0f) << 4));
                            }
                        }

                        destaddress = (char) (destaddress + 128);
                        srcaddress = (char) (srcaddress + 8);
                    }
                } else {
                    for (i = 0; i < 16; i++) {
                        for (int x = 0; x <= 8; x++) {
                            if ((JunoBLTRom.read(srcaddress + x) & 0xF0) != 0) {
                                tutankhm_videoram_w.handler(destaddress + x, videoram.read(destaddress + x) & 0xF0);
                            }
                            if ((JunoBLTRom.read(srcaddress + x) & 0x0F) != 0) {
                                tutankhm_videoram_w.handler(destaddress + x, videoram.read(destaddress + x) & 0x0F);
                            }
                        }
                        destaddress = (char) (destaddress + 128);
                        srcaddress = (char) (srcaddress + 8);
                    }
                }
            }
        }
    };
}
