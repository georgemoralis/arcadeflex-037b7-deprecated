/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */
package gr.codebb.arcadeflex.beta.v037b7.vidhrdw;

import gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.avgdvg.*;
import static gr.codebb.arcadeflex.WIP.v037b7.vidhrdw.vector.*;
import gr.codebb.arcadeflex.common.PtrLib.UBytePtr;
import static gr.codebb.arcadeflex.v037b7.common.fucPtr.*;

public class llander {

    public static final int NUM_LIGHTS = 5;

    /*TODO*///	static struct artwork_info *llander_panel;
/*TODO*///		static struct artwork_info *llander_lit_panel;

    /*TODO*///	static struct rectangle light_areas[NUM_LIGHTS] =
/*TODO*///	{
/*TODO*///		{  0, 205, 0, 127 },
/*TODO*///		{206, 343, 0, 127 },
/*TODO*///		{344, 481, 0, 127 },
/*TODO*///		{482, 616, 0, 127 },
/*TODO*///		{617, 799, 0, 127 },
/*TODO*///	};

    /* current status of each light */
    static int[] lights = new int[NUM_LIGHTS];
    /* whether or not each light needs to be redrawn*/
    static int[] lights_changed = new int[NUM_LIGHTS];
    /**
     * *************************************************************************
     * Lunar Lander video routines
     **************************************************************************
     */
    public static VhConvertColorPromPtr llander_init_colors = new VhConvertColorPromPtr() {
        public void handler(char[] palette, char[] colortable, UBytePtr color_prom) {
            int width, height, i, nextcol;

            avg_init_palette_white.handler(palette, colortable, color_prom);

            /*TODO*///			llander_lit_panel = NULL;
/*TODO*///			width = Machine.scrbitmap.width;
/*TODO*///			height = 0.16 * width;
            /*TODO*///			nextcol = 24;
            /*TODO*///			artwork_load_size(&llander_panel, "llander.png", nextcol, Machine.drv.total_colors-nextcol, width, height);
/*TODO*///			if (llander_panel != NULL)
/*TODO*///			{
/*TODO*///				if (Machine.scrbitmap.depth == 8)
/*TODO*///					nextcol += llander_panel.num_pens_used;
/*TODO*///
/*TODO*///				artwork_load_size(&llander_lit_panel, "llander1.png", nextcol, Machine.drv.total_colors-nextcol, width, height);
/*TODO*///				if (llander_lit_panel == NULL)
/*TODO*///				{
/*TODO*///					artwork_free (&llander_panel);
/*TODO*///					return ;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else
/*TODO*///				return;
            /*TODO*///			for (i = 0; i < 16; i++)
/*TODO*///				palette[3*(i+8)]=palette[3*(i+8)+1]=palette[3*(i+8)+2]= (255*i)/15;
            /*TODO*///			memcpy (palette+3*llander_panel.start_pen, llander_panel.orig_palette,
/*TODO*///					3*llander_panel.num_pens_used);
/*TODO*///			memcpy (palette+3*llander_lit_panel.start_pen, llander_lit_panel.orig_palette,
/*TODO*///					3*llander_lit_panel.num_pens_used);
        }
    };

    public static VhStartPtr llander_start = new VhStartPtr() {
        public int handler() {
            int i;

            if (dvg_start.handler() != 0) {
                return 1;
            }

            /*TODO*///			if (llander_panel == NULL)
            return 0;

            /*TODO*///			for (i=0;i<NUM_LIGHTS;i++)
/*TODO*///			{
/*TODO*///				lights[i] = 0;
/*TODO*///				lights_changed[i] = 1;
/*TODO*///			}
/*TODO*///			if (llander_panel != 0) backdrop_refresh(llander_panel);
/*TODO*///			if (llander_lit_panel != 0) backdrop_refresh(llander_lit_panel);
/*TODO*///			return 0;
        }
    };
    public static VhStopPtr llander_stop = new VhStopPtr() {
        public void handler() {
            dvg_stop.handler();

            /*TODO*///			if (llander_panel != NULL)
/*TODO*///				artwork_free(&llander_panel);
            /*TODO*///			if (llander_lit_panel != NULL)
/*TODO*///				artwork_free(&llander_lit_panel);
        }
    };
    public static VhUpdatePtr llander_screenrefresh = new VhUpdatePtr() {
        public void handler(osd_bitmap bitmap, int full_refresh) {
            int i, pwidth, pheight;
            float scale;
            /*TODO*///			struct osd_bitmap vector_bitmap;
/*TODO*///			struct rectangle rect;

            /*TODO*///			if (llander_panel == NULL)
/*TODO*///			{
            vector_vh_screenrefresh.handler(bitmap, full_refresh);
            /*TODO*///				return;
/*TODO*///			}

            /*TODO*///			pwidth = llander_panel.artwork.width;
/*TODO*///			pheight = llander_panel.artwork.height;
            /*TODO*///			vector_bitmap.width = bitmap.width;
/*TODO*///			vector_bitmap.height = bitmap.height - pheight;
/*TODO*///			vector_bitmap._private = bitmap._private;
/*TODO*///			vector_bitmap.line = bitmap.line;
            /*TODO*///			vector_vh_screenrefresh(&vector_bitmap,full_refresh);
            /*TODO*///			if (full_refresh != 0)
/*TODO*///			{
/*TODO*///				rect.min_x = 0;
/*TODO*///				rect.max_x = pwidth-1;
/*TODO*///				rect.min_y = bitmap.height - pheight;
/*TODO*///				rect.max_y = bitmap.height - 1;
            /*TODO*///				copybitmap(bitmap,llander_panel.artwork,0,0,
/*TODO*///						   0,bitmap.height - pheight,&rect,TRANSPARENCY_NONE,0);
/*TODO*///				osd_mark_dirty (rect.min_x,rect.min_y,rect.max_x,rect.max_y,0);
/*TODO*///			}
            /*TODO*///			scale = pwidth/800.0;
            /*TODO*///			for (i=0;i<NUM_LIGHTS;i++)
/*TODO*///			{
/*TODO*///				if (lights_changed[i] || full_refresh)
/*TODO*///				{
/*TODO*///					rect.min_x = scale * light_areas[i].min_x;
/*TODO*///					rect.max_x = scale * light_areas[i].max_x;
/*TODO*///					rect.min_y = bitmap.height - pheight + scale * light_areas[i].min_y;
/*TODO*///					rect.max_y = bitmap.height - pheight + scale * light_areas[i].max_y;
/*TODO*///
/*TODO*///					if (lights[i])
/*TODO*///						copybitmap(bitmap,llander_lit_panel.artwork,0,0,
/*TODO*///								   0,bitmap.height - pheight,&rect,TRANSPARENCY_NONE,0);
/*TODO*///					else
/*TODO*///						copybitmap(bitmap,llander_panel.artwork,0,0,
/*TODO*///								   0,bitmap.height - pheight,&rect,TRANSPARENCY_NONE,0);
/*TODO*///
/*TODO*///					osd_mark_dirty (rect.min_x,rect.min_y,rect.max_x,rect.max_y,0);
/*TODO*///
/*TODO*///					lights_changed[i] = 0;
/*TODO*///				}
/*TODO*///			}
        }
    };

    /* Lunar lander LED port seems to be mapped thus:
	
	   NNxxxxxx - Apparently unused
	   xxNxxxxx - Unknown gives 4 high pulses of variable duration when coin put in ?
	   xxxNxxxx - Start    Lamp ON/OFF == 0/1
	   xxxxNxxx - Training Lamp ON/OFF == 1/0
	   xxxxxNxx - Cadet    Lamp ON/OFF
	   xxxxxxNx - Prime    Lamp ON/OFF
	   xxxxxxxN - Command  Lamp ON/OFF
	
	   Selection lamps seem to all be driver 50/50 on/off during attract mode ?
	
     */
    public static WriteHandlerPtr llander_led_w = new WriteHandlerPtr() {
        public void handler(int offset, int data) {
            /*      logerror("LANDER LED: %02x\n",data); */

            int i;

            for (i = 0; i < 5; i++) {
                int new_light = (data & (1 << (4 - i))) != 0 ? 1 : 0;
                if (lights[i] != new_light) {
                    lights[i] = new_light;
                    lights_changed[i] = 1;
                }
            }
        }
    };

}
