package gr.codebb.arcadeflex.old.arcadeflex;

import static gr.codebb.arcadeflex.common.libc.cstdio.*;
import static gr.codebb.arcadeflex.common.SubArrays.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.osdependH.osd_bitmap;
import static gr.codebb.arcadeflex.old.arcadeflex.blit.*;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.*;
import static gr.codebb.arcadeflex.old.arcadeflex.sound.update_audio;
import static gr.codebb.arcadeflex.old.arcadeflex.ticker.TICKS_PER_SEC;
import static gr.codebb.arcadeflex.old.arcadeflex.ticker.ticker;
import static gr.codebb.arcadeflex.old.arcadeflex.video.*;
import static gr.codebb.arcadeflex.old.arcadeflex.video.osd_skip_this_frame;
import static gr.codebb.arcadeflex.old.arcadeflex.video.pan_display;
import static gr.codebb.arcadeflex.old.mame.input.input_ui_pressed;
import static gr.codebb.arcadeflex.old.mame.usrintrf.set_ui_visarea;
import static gr.codebb.arcadeflex.old.mame.usrintrf.ui_text;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.mame.*;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.drawgfxH.rectangle;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.inptportH.*;
import static gr.codebb.arcadeflex.v037b7.mame.version.build_version;

import gr.codebb.arcadeflex.old2.arcadeflex.settings;
import java.awt.*;
import java.awt.event.*;

public class video_old {

    static FILE errorlog = null;
    public static software_gfx screen; //for our screen creation

    public static class RGB {

        char r, g, b;
    }

    /*TODO*///static int gone_to_gfx_mode;

    /*TODO*///							/* to avoid counting the copyright and info screens */
    /*TODO*///

    /* Create a bitmap. Also calls osd_clearbitmap() to appropriately initialize */
 /* it to the background color. */
 /* VERY IMPORTANT: the function must allocate also a "safety area" 16 pixels wide all */
 /* around the bitmap. This is required because, for performance reasons, some graphic */
 /* routines don't clip at boundaries of the bitmap. */
 /*TODO*///
    public static void select_display_mode(int depth) {
        int width, height;

        auto_resolution = 0;
        /*TODO*///	/* assume unchained video mode  */
        /*TODO*///	unchained = 0;
        /*TODO*///	/* see if it's a low scanrate mode */
        /*TODO*///	switch (monitor_type)
        /*TODO*///	{
        /*TODO*///		case MONITOR_TYPE_NTSC:
        /*TODO*///		case MONITOR_TYPE_PAL:
        /*TODO*///		case MONITOR_TYPE_ARCADE:
        /*TODO*///			scanrate15KHz = 1;
        /*TODO*///			break;
        /*TODO*///		default:
        /*TODO*///			scanrate15KHz = 0;
        /*TODO*///	}
        /*TODO*///
        /*TODO*///	/* initialise quadring table [useful for *all* doubling modes */
        /*TODO*///	for (i = 0; i < 256; i++)
        /*TODO*///	{
        /*TODO*///		doublepixel[i] = i | (i<<8);
        /*TODO*///		quadpixel[i] = i | (i<<8) | (i << 16) | (i << 24);
        /*TODO*///	}
        /*TODO*///
        if (vector_game != 0) {
            width = Machine.drv.screen_width;
            height = Machine.drv.screen_height;
        } else {
            width = Machine.visible_area.max_x - Machine.visible_area.min_x + 1;
            height = Machine.visible_area.max_y - Machine.visible_area.min_y + 1;
        }

        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
            int temp;

            temp = width;
            width = height;
            height = temp;
        }

        use_vesa = -1;

        /* If no VESA resolution has been given, we choose a sensible one. */
 /* 640x480, 800x600 and 1024x768 are common to all VESA drivers_old. */
        if (gfx_width == 0 && gfx_height == 0) {
            auto_resolution = 1;
            use_vesa = 1;

            /* vector games use 640x480 as default */
            if (vector_game != 0) {
                gfx_width = 640;
                gfx_height = 480;
            } else {
                int xm, ym;

                xm = ym = 1;

                if ((Machine.drv.video_attributes & VIDEO_PIXEL_ASPECT_RATIO_MASK)
                        == VIDEO_PIXEL_ASPECT_RATIO_1_2) {
                    if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
                        xm++;
                    } else {
                        ym++;
                    }
                }

                if (scanlines != 0 && stretch != 0) {
                    if (ym == 1) {
                        xm *= 2;
                        ym *= 2;
                    }

                    /* see if pixel doubling can be applied at 640x480 */
                    if (ym * height <= 480 && xm * width <= 640
                            && (xm > 1 || (ym + 1) * height > 768 || (xm + 1) * width > 1024)) {
                        gfx_width = 640;
                        gfx_height = 480;
                    } /* see if pixel doubling can be applied at 800x600 */ else if (ym * height <= 600 && xm * width <= 800
                            && (xm > 1 || (ym + 1) * height > 768 || (xm + 1) * width > 1024)) {
                        gfx_width = 800;
                        gfx_height = 600;
                    }
                    /* don't use 1024x768 right away. If 512x384 is available, it */
 /* will provide hardware scanlines. */

                    if (ym > 1 && xm > 1) {
                        xm /= 2;
                        ym /= 2;
                    }
                }

                if (gfx_width == 0 && gfx_height == 0) {
                    if (ym * height <= 240 && xm * width <= 320) {
                        gfx_width = 320;
                        gfx_height = 240;
                    } else if (ym * height <= 300 && xm * width <= 400) {
                        gfx_width = 400;
                        gfx_height = 300;
                    } else if (ym * height <= 384 && xm * width <= 512) {
                        gfx_width = 512;
                        gfx_height = 384;
                    } else if (ym * height <= 480 && xm * width <= 640
                            && (stretch == 0 || (ym + 1) * height > 768 || (xm + 1) * width > 1024)) {
                        gfx_width = 640;
                        gfx_height = 480;
                    } else if (ym * height <= 600 && xm * width <= 800
                            && (stretch == 0 || (ym + 1) * height > 768 || (xm + 1) * width > 1024)) {
                        gfx_width = 800;
                        gfx_height = 600;
                    } else {
                        gfx_width = 1024;
                        gfx_height = 768;
                    }
                }
            }
        }
    }

    /*TODO*///
    /*TODO*///
    /*TODO*///
    /* center image inside the display based on the visual area */

    public static void adjust_display(int xmin, int ymin, int xmax, int ymax, int depth) {
        int temp;
        int w, h;
        int act_width;

        act_width = gfx_width;

        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
            temp = xmin;
            xmin = ymin;
            ymin = temp;
            temp = xmax;
            xmax = ymax;
            ymax = temp;
            w = Machine.drv.screen_height;
            h = Machine.drv.screen_width;
        } else {
            w = Machine.drv.screen_width;
            h = Machine.drv.screen_height;
        }

        if (vector_game == 0) {
            if ((Machine.orientation & ORIENTATION_FLIP_X) != 0) {
                temp = w - xmin - 1;
                xmin = w - xmax - 1;
                xmax = temp;
            }
            if ((Machine.orientation & ORIENTATION_FLIP_Y) != 0) {
                temp = h - ymin - 1;
                ymin = h - ymax - 1;
                ymax = temp;
            }
        }

        viswidth = xmax - xmin + 1;
        visheight = ymax - ymin + 1;

        /* setup xmultiply to handle SVGA driver's (possible) double width */
        xmultiply = act_width / gfx_width;
        ymultiply = 1;

        if (use_vesa != 0 && vector_game == 0) {
            if (stretch != 0) {
                if ((Machine.orientation & ORIENTATION_SWAP_XY) == 0
                        && (Machine.drv.video_attributes & VIDEO_DUAL_MONITOR) == 0) {
                    /* horizontal, non dual monitor games may be stretched at will */
                    while ((xmultiply + 1) * viswidth <= act_width) {
                        xmultiply++;
                    }
                    while ((ymultiply + 1) * visheight <= gfx_height) {
                        ymultiply++;
                    }
                } else {
                    int tw, th;

                    tw = act_width;
                    th = gfx_height;

                    if ((Machine.drv.video_attributes & VIDEO_PIXEL_ASPECT_RATIO_MASK)
                            == VIDEO_PIXEL_ASPECT_RATIO_1_2) {
                        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
                            tw /= 2;
                        } else {
                            th /= 2;
                        }
                    }

                    /* Hack for 320x480 and 400x600 "vmame" video modes */
                    if ((gfx_width == 320 && gfx_height == 480)
                            || (gfx_width == 400 && gfx_height == 600)) {
                        th /= 2;
                    }

                    /* maintain aspect ratio for other games */
                    while ((xmultiply + 1) * viswidth <= tw
                            && (ymultiply + 1) * visheight <= th) {
                        xmultiply++;
                        ymultiply++;
                    }

                    if ((Machine.drv.video_attributes & VIDEO_PIXEL_ASPECT_RATIO_MASK)
                            == VIDEO_PIXEL_ASPECT_RATIO_1_2) {
                        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
                            xmultiply *= 2;
                        } else {
                            ymultiply *= 2;
                        }
                    }

                    /* Hack for 320x480 and 400x600 "vmame" video modes */
                    if ((gfx_width == 320 && gfx_height == 480)
                            || (gfx_width == 400 && gfx_height == 600)) {
                        ymultiply *= 2;
                    }
                }
            } else {
                if ((Machine.drv.video_attributes & VIDEO_PIXEL_ASPECT_RATIO_MASK)
                        == VIDEO_PIXEL_ASPECT_RATIO_1_2) {
                    if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
                        xmultiply *= 2;
                    } else {
                        ymultiply *= 2;
                    }
                }

                /* Hack for 320x480 and 400x600 "vmame" video modes */
                if ((gfx_width == 320 && gfx_height == 480)
                        || (gfx_width == 400 && gfx_height == 600)) {
                    ymultiply *= 2;
                }
            }
        }

        if (depth == 16) {
            if (xmultiply > MAX_X_MULTIPLY16) {
                xmultiply = MAX_X_MULTIPLY16;
            }
            if (ymultiply > MAX_Y_MULTIPLY16) {
                ymultiply = MAX_Y_MULTIPLY16;
            }
        } else {
            if (xmultiply > MAX_X_MULTIPLY) {
                xmultiply = MAX_X_MULTIPLY;
            }
            if (ymultiply > MAX_Y_MULTIPLY) {
                ymultiply = MAX_Y_MULTIPLY;
            }
        }

        gfx_display_lines = visheight;
        gfx_display_columns = viswidth;

        gfx_xoffset = (act_width - viswidth * xmultiply) / 2;
        if (gfx_display_columns > act_width / xmultiply) {
            gfx_display_columns = act_width / xmultiply;
        }

        gfx_yoffset = (gfx_height - visheight * ymultiply) / 2;
        if (gfx_display_lines > gfx_height / ymultiply) {
            gfx_display_lines = gfx_height / ymultiply;
        }

        skiplinesmin = ymin;
        skiplinesmax = visheight - gfx_display_lines + ymin;
        skipcolumnsmin = xmin;
        skipcolumnsmax = viswidth - gfx_display_columns + xmin;

        /* Align on a quadword !*/
        gfx_xoffset &= ~7;

        /* the skipcolumns from mame_old.cfg/cmdline is relative to the visible area */
        skipcolumns = xmin + skipcolumns;
        skiplines = ymin + skiplines;

        /* Just in case the visual area doesn't fit */
        if (gfx_xoffset < 0) {
            skipcolumns -= gfx_xoffset;
            gfx_xoffset = 0;
        }
        if (gfx_yoffset < 0) {
            skiplines -= gfx_yoffset;
            gfx_yoffset = 0;
        }

        /* Failsafe against silly parameters */
        if (skiplines < skiplinesmin) {
            skiplines = skiplinesmin;
        }
        if (skipcolumns < skipcolumnsmin) {
            skipcolumns = skipcolumnsmin;
        }
        if (skiplines > skiplinesmax) {
            skiplines = skiplinesmax;
        }
        if (skipcolumns > skipcolumnsmax) {
            skipcolumns = skipcolumnsmax;
        }

        if (errorlog != null) {
            fprintf(errorlog,
                    "gfx_width = %d gfx_height = %d\n"
                    + "gfx_xoffset = %d gfx_yoffset = %d\n"
                    + "xmin %d ymin %d xmax %d ymax %d\n"
                    + "skiplines %d skipcolumns %d\n"
                    + "gfx_display_lines %d gfx_display_columns %d\n"
                    + "xmultiply %d ymultiply %d\n",
                    gfx_width, gfx_height,
                    gfx_xoffset, gfx_yoffset,
                    xmin, ymin, xmax, ymax, skiplines, skipcolumns, gfx_display_lines, gfx_display_columns, xmultiply, ymultiply);
        }

        set_ui_visarea(skipcolumns, skiplines, skipcolumns + gfx_display_columns - 1, skiplines + gfx_display_lines - 1);

        /* round to a multiple of 4 to avoid missing pixels on the right side */
        gfx_display_columns = (gfx_display_columns + 3) & ~3;
    }


    /* Create a display screen, or window, large enough to accomodate a bitmap */
 /* of the given dimensions. Attributes are the ones defined in driver.h. */
 /* Return a osd_bitmap pointer or 0 in case of error. */
    public static osd_bitmap osd_create_display(int width, int height, int depth, int attributes) {
        if (errorlog != null) {
            fprintf(errorlog, "width %d, height %d\n", width, height);
        }

        brightness = 100;
        brightness_paused_adjust = 1.0f;
        dirty_bright = 1;

        if (frameskip < 0) {
            frameskip = 0;
        }
        if (frameskip >= FRAMESKIP_LEVELS) {
            frameskip = FRAMESKIP_LEVELS - 1;
        }

        /*TODO????*///	gone_to_gfx_mode = 0;
        /* Look if this is a vector game */
        if ((Machine.drv.video_attributes & VIDEO_TYPE_VECTOR) != 0) {
            vector_game = 1;
        } else {
            vector_game = 0;
        }

        if (use_dirty == -1) /* dirty=auto in mame_old.cfg? */ {
            /* Is the game using a dirty system? */
            if (((Machine.drv.video_attributes & VIDEO_SUPPORTS_DIRTY) != 0) || (vector_game != 0)) {
                use_dirty = 1;
            } else {
                use_dirty = 0;
            }
        }

        select_display_mode(depth);

        if (vector_game != 0) {
            throw new UnsupportedOperationException("Unsupported scale_vectorgames");
            /*TODO*///		scale_vectorgames(gfx_width,gfx_height,&width, &height);
        }

        if (osd_set_display(width, height, attributes) == 0) {
            return null;
        }

        /* center display based on visible area */
        if (vector_game != 0) {
            adjust_display(0, 0, width - 1, height - 1, depth);
        } else {
            rectangle vis = Machine.visible_area;
            adjust_display(vis.min_x, vis.min_y, vis.max_x, vis.max_y, depth);
        }

        return Machine.scrbitmap;

    }

    /*TODO*///
    /*TODO*////* set the actual display screen but don't allocate the screen bitmap */

    public static int osd_set_display(int width, int height, int attributes) {
        /*TODO*///	struct mode_adjust *adjust_array;
        /*TODO*///
        int i;
        /* moved 'found' to here (req. for 15.75KHz Arcade Monitor Modes) */
        int found;

        if (gfx_height == 0 || gfx_width == 0) {
            printf("Please specify height AND width (e.g. -640x480)\n");
            return 0;
        }

        if ((Machine.orientation & ORIENTATION_SWAP_XY) != 0) {
            int temp;

            temp = width;
            width = height;
            height = temp;
        }
        /* Mark the dirty buffers as dirty */

 /*TODO*///	if (use_dirty)
        /*TODO*///	{
        /*TODO*///		if (vector_game)
        /*TODO*///			/* vector games only use one dirty buffer */
        /*TODO*///			init_dirty (0);
        /*TODO*///		else
        /*TODO*///			init_dirty(1);
        /*TODO*///		swap_dirty();
        /*TODO*///		init_dirty(1);
        /*TODO*///	}
        /*TODO*///	if (dirtycolor)
        /*TODO*///	{
        /*TODO*///		for (i = 0;i < screen_colors;i++)
        /*TODO*///			dirtycolor[i] = 1;
        /*TODO*///		dirtypalette = 1;
        /*TODO*///	}
        /*TODO*///	/* handle special 15.75KHz modes, these now include SVGA modes */
        /*TODO*///	found = 0;
        /*TODO*///	/*move video freq set to here, as we need to set it explicitly for the 15.75KHz modes */
        /*TODO*///	videofreq = vgafreq;
        /*TODO*///
        /*TODO*///	if (use_vesa != 0)
        /*TODO*///	{
        /*TODO*///		/*removed local 'found' */
        /*TODO*///		int mode, bits, err;
        /*TODO*///
        /*TODO*///		mode = gfx_mode;
        /*TODO*///		found = 0;
        /*TODO*///		bits = scrbitmap->depth;
        /*TODO*///
        /*TODO*///		/* Try the specified vesamode, 565 and 555 for 16 bit color modes, */
        /*TODO*///		/* doubled resolution in case of noscanlines and if not succesful  */
        /*TODO*///		/* repeat for all "lower" VESA modes. NS/BW 19980102 */
        /*TODO*///
        /*TODO*///		while (!found)
        /*TODO*///		{
        /*TODO*///			set_color_depth(bits);
        /*TODO*///
        /*TODO*///			/* allocate a wide enough virtual screen if possible */
        /*TODO*///			/* we round the width (in dwords) to be an even multiple 256 - that */
        /*TODO*///			/* way, during page flipping only one byte of the video RAM */
        /*TODO*///			/* address changes, therefore preventing flickering. */
        /*TODO*///			if (bits == 8)
        /*TODO*///				triplebuf_page_width = (gfx_width + 0x3ff) & ~0x3ff;
        /*TODO*///			else
        /*TODO*///				triplebuf_page_width = (gfx_width + 0x1ff) & ~0x1ff;
        /*TODO*///
        /*TODO*///			/* don't ask for a larger screen if triplebuffer not requested - could */
        /*TODO*///			/* cause problems in some cases. */
        /*TODO*///			err = 1;
        /*TODO*///			if (use_triplebuf)
        /*TODO*///				err = set_gfx_mode(mode,gfx_width,gfx_height,3*triplebuf_page_width,0);
        /*TODO*///			if (err)
        /*TODO*///			{
        /*TODO*///				/* if we're using a SVGA 15KHz driver - tell Allegro the virtual screen width */
        /*TODO*///				if(SVGA15KHzdriver)
        /*TODO*///					err = set_gfx_mode(mode,gfx_width,gfx_height,SVGA15KHzdriver->getlogicalwidth(gfx_width),0);
        /*TODO*///				else
        /*TODO*///					err = set_gfx_mode(mode,gfx_width,gfx_height,0,0);
        /*TODO*///			}
        /*TODO*///
        /*TODO*///			if (errorlog)
        /*TODO*///			{
        /*TODO*///				fprintf (errorlog,"Trying ");
        /*TODO*///				if      (mode == GFX_VESA1)
        /*TODO*///					fprintf (errorlog, "VESA1");
        /*TODO*///				else if (mode == GFX_VESA2B)
        /*TODO*///					fprintf (errorlog, "VESA2B");
        /*TODO*///				else if (mode == GFX_VESA2L)
        /*TODO*///				    fprintf (errorlog, "VESA2L");
        /*TODO*///				else if (mode == GFX_VESA3)
        /*TODO*///					fprintf (errorlog, "VESA3");
        /*TODO*///			    fprintf (errorlog, "  %dx%d, %d bit\n",
        /*TODO*///						gfx_width, gfx_height, bits);
        /*TODO*///			}
        /*TODO*///
        /*TODO*///			if (err == 0)
        /*TODO*///			{
        /*TODO*///				found = 1;
        /*TODO*///				/* replace gfx_mode with found mode */
        /*TODO*///				gfx_mode = mode;
        /*TODO*///				continue;
        /*TODO*///			}
        /*TODO*///			else if (errorlog)
        /*TODO*///				fprintf (errorlog,"%s\n",allegro_error);
        /*TODO*///
        /*TODO*///			/* Now adjust parameters for the next loop */
        /*TODO*///
        /*TODO*///			/* try 5-5-5 in case there is no 5-6-5 16 bit color mode */
        /*TODO*///			if (scrbitmap->depth == 16)
        /*TODO*///			{
        /*TODO*///				if (bits == 16)
        /*TODO*///				{
        /*TODO*///					bits = 15;
        /*TODO*///					continue;
        /*TODO*///				}
        /*TODO*///				else
        /*TODO*///					bits = 16; /* reset to 5-6-5 */
        /*TODO*///			}
        /*TODO*///
        /*TODO*///			/* try VESA modes in VESA3-VESA2L-VESA2B-VESA1 order */
        /*TODO*///
        /*TODO*///			if (mode == GFX_VESA3)
        /*TODO*///			{
        /*TODO*///				mode = GFX_VESA2L;
        /*TODO*///				continue;
        /*TODO*///			}
        /*TODO*///			else if (mode == GFX_VESA2L)
        /*TODO*///			{
        /*TODO*///				mode = GFX_VESA2B;
        /*TODO*///				continue;
        /*TODO*///			}
        /*TODO*///			else if (mode == GFX_VESA2B)
        /*TODO*///			{
        /*TODO*///				mode = GFX_VESA1;
        /*TODO*///				continue;
        /*TODO*///			}
        /*TODO*///			else if (mode == GFX_VESA1)
        /*TODO*///				mode = gfx_mode; /* restart with the mode given in mame_old.cfg */
        /*TODO*///
        /*TODO*///			/* try higher resolutions */
        /*TODO*///			if (auto_resolution)
        /*TODO*///			{
        /*TODO*///				if (stretch && gfx_width <= 512)
        /*TODO*///				{
        /*TODO*///					/* low res VESA mode not available, try an high res one */
        /*TODO*///					gfx_width *= 2;
        /*TODO*///					gfx_height *= 2;
        /*TODO*///					continue;
        /*TODO*///				}
        /*TODO*///
        /*TODO*///				/* try next higher resolution */
        /*TODO*///				if (gfx_height < 300 && gfx_width < 400)
        /*TODO*///				{
        /*TODO*///					gfx_width = 400;
        /*TODO*///					gfx_height = 300;
        /*TODO*///					continue;
        /*TODO*///				}
        /*TODO*///				else if (gfx_height < 384 && gfx_width < 512)
        /*TODO*///				{
        /*TODO*///					gfx_width = 512;
        /*TODO*///					gfx_height = 384;
        /*TODO*///					continue;
        /*TODO*///				}
        /*TODO*///				else if (gfx_height < 480 && gfx_width < 640)
        /*TODO*///				{
        /*TODO*///					gfx_width = 640;
        /*TODO*///					gfx_height = 480;
        /*TODO*///					continue;
        /*TODO*///				}
        /*TODO*///				else if (gfx_height < 600 && gfx_width < 800)
        /*TODO*///				{
        /*TODO*///					gfx_width = 800;
        /*TODO*///					gfx_height = 600;
        /*TODO*///					continue;
        /*TODO*///				}
        /*TODO*///				else if (gfx_height < 768 && gfx_width < 1024)
        /*TODO*///				{
        /*TODO*///					gfx_width = 1024;
        /*TODO*///					gfx_height = 768;
        /*TODO*///					continue;
        /*TODO*///				}
        /*TODO*///			}
        /*TODO*///
        /*TODO*///			/* If there was no continue up to this point, we give up */
        /*TODO*///			break;
        /*TODO*///		}
        /*TODO*///
        /*TODO*///		if (found == 0)
        /*TODO*///		{
        /*TODO*///			printf ("\nNo %d-bit %dx%d VESA mode available.\n",
        /*TODO*///					scrbitmap->depth,gfx_width,gfx_height);
        /*TODO*///			printf ("\nPossible causes:\n"
        /*TODO*///"1) Your video card does not support VESA modes at all. Almost all\n"
        /*TODO*///"   video cards support VESA modes natively these days, so you probably\n"
        /*TODO*///"   have an older card which needs some driver loaded first.\n"
        /*TODO*///"   In case you can't find such a driver in the software that came with\n"
        /*TODO*///"   your video card, Scitech Display Doctor or (for S3 cards) S3VBE\n"
        /*TODO*///"   are good alternatives.\n"
        /*TODO*///"2) Your VESA implementation does not support this resolution. For example,\n"
        /*TODO*///"   '-320x240', '-400x300' and '-512x384' are only supported by a few\n"
        /*TODO*///"   implementations.\n"
        /*TODO*///"3) Your video card doesn't support this resolution at this color depth.\n"
        /*TODO*///"   For example, 1024x768 in 16 bit colors requires 2MB video memory.\n"
        /*TODO*///"   You can either force an 8 bit video mode ('-depth 8') or use a lower\n"
        /*TODO*///"   resolution ('-640x480', '-800x600').\n");
        /*TODO*///			return 0;
        /*TODO*///		}
        /*TODO*///		else
        /*TODO*///		{
        /*TODO*///			if (errorlog)
        /*TODO*///				fprintf (errorlog, "Found matching %s mode\n", gfx_driver->desc);
        /*TODO*///			gfx_mode = mode;
        /*TODO*///			/* disable triple buffering if the screen is not large enough */
        /*TODO*///			if (errorlog)
        /*TODO*///				fprintf (errorlog, "Virtual screen size %dx%d\n",VIRTUAL_W,VIRTUAL_H);
        /*TODO*///			if (VIRTUAL_W < 3*triplebuf_page_width)
        /*TODO*///			{
        /*TODO*///				use_triplebuf = 0;
        /*TODO*///				if (errorlog)
        /*TODO*///					fprintf (errorlog, "Triple buffer disabled\n");
        /*TODO*///			}
        /*TODO*///
        /*TODO*///			/* if triple buffering is enabled, turn off vsync */
        /*TODO*///			if (use_triplebuf)
        /*TODO*///			{
        /*TODO*///				wait_vsync = 0;
        /*TODO*///				video_sync = 0;
        /*TODO*///			}
        /*TODO*///		}
        /*TODO*///	}
        /*TODO*///	else
        /*TODO*///	{
        /*TODO*///
        /*TODO*///
        /*TODO*///		/* set the VGA clock */
        /*TODO*///		if (video_sync || always_synced || wait_vsync)
        /*TODO*///			reg[0].value = (reg[0].value & 0xf3) | (videofreq << 2);
        /*TODO*///
        /*TODO*///		/* VGA triple buffering */
        /*TODO*///		if(use_triplebuf)
        /*TODO*///		{
        /*TODO*///
        /*TODO*///			int vga_page_size = (gfx_width * gfx_height);
        /*TODO*///			/* see if it'll fit */
        /*TODO*///			if ((vga_page_size * 3) > 0x40000)
        /*TODO*///			{
        /*TODO*///				/* too big */
        /*TODO*///				if (errorlog)
        /*TODO*///					fprintf(errorlog,"tweaked mode %dx%d is too large to triple buffer\ntriple buffering disabled\n",gfx_width,gfx_height);
        /*TODO*///				use_triplebuf = 0;
        /*TODO*///			}
        /*TODO*///			else
        /*TODO*///			{
        /*TODO*///				/* it fits, so set up the 3 pages */
        /*TODO*///				no_xpages = 3;
        /*TODO*///				xpage_size = vga_page_size / 4;
        /*TODO*///				if (errorlog)
        /*TODO*///					fprintf(errorlog,"unchained VGA triple buffering page size :%d\n",xpage_size);
        /*TODO*///				/* and make sure the mode's unchained */
        /*TODO*///				unchain_vga (reg);
        /*TODO*///				/* triple buffering is enabled, turn off vsync */
        /*TODO*///				wait_vsync = 0;
        /*TODO*///				video_sync = 0;
        /*TODO*///			}
        /*TODO*///		}
        /*TODO*///		/* center the mode */
        /*TODO*///		center_mode (reg);
        /*TODO*///
        /*TODO*///		/* set the horizontal and vertical total */
        /*TODO*///		if (scanrate15KHz)
        /*TODO*///			/* 15.75KHz modes */
        /*TODO*///			adjust_array = arcade_adjust;
        /*TODO*///		else
        /*TODO*///			/* PC monitor modes */
        /*TODO*///			adjust_array = pc_adjust;
        /*TODO*///
        /*TODO*///		for (i=0; adjust_array[i].x != 0; i++)
        /*TODO*///		{
        /*TODO*///			if ((gfx_width == adjust_array[i].x) && (gfx_height == adjust_array[i].y))
        /*TODO*///			{
        /*TODO*///				/* check for 'special vertical' modes */
        /*TODO*///				if((!adjust_array[i].vertical_mode && !(Machine->orientation & ORIENTATION_SWAP_XY)) ||
        /*TODO*///					(adjust_array[i].vertical_mode && (Machine->orientation & ORIENTATION_SWAP_XY)))
        /*TODO*///				{
        /*TODO*///					reg[H_TOTAL_INDEX].value = *adjust_array[i].hadjust;
        /*TODO*///					reg[V_TOTAL_INDEX].value = *adjust_array[i].vadjust;
        /*TODO*///					break;
        /*TODO*///				}
        /*TODO*///			}
        /*TODO*///		}
        /*TODO*///
        /*TODO*///		/*if scanlines were requested - change the array values to get a scanline mode */
        /*TODO*///		if (scanlines && !scanrate15KHz)
        /*TODO*///			reg = make_scanline_mode(reg,reglen);
        /*TODO*///
        /*TODO*///		/* big hack: open a mode 13h screen using Allegro, then load the custom screen */
        /*TODO*///		/* definition over it. */
        /*TODO*///		if (set_gfx_mode(GFX_VGA,320,200,0,0) != 0)
        /*TODO*///			return 0;
        /*TODO*///
        /*TODO*///		if (errorlog)
        /*TODO*///		{
        /*TODO*///			fprintf(errorlog,"Generated Tweak Values :-\n");
        /*TODO*///			for (i=0; i<reglen; i++)
        /*TODO*///			{
        /*TODO*///				fprintf(errorlog,"{ 0x%02x, 0x%02x, 0x%02x},",reg[i].port,reg[i].index,reg[i].value);
        /*TODO*///				if (!((i+1)%3))
        /*TODO*///					fprintf(errorlog,"\n");
        /*TODO*///			}
        /*TODO*///		}
        /*TODO*///
        /*TODO*///		/* tweak the mode */
        /*TODO*///		outRegArray(reg,reglen);
        /*TODO*///
        /*TODO*///		/* check for unchained mode,  if unchained clear all pages */
        /*TODO*///		if (unchained)
        /*TODO*///		{
        /*TODO*///			unsigned long address;
        /*TODO*///			/* clear all 4 bit planes */
        /*TODO*///			outportw (0x3c4, (0x02 | (0x0f << 0x08)));
        /*TODO*///			for (address = 0xa0000; address < 0xb0000; address += 4)
        /*TODO*///				_farpokel(screen->seg, address, 0);
        /*TODO*///		}
        /*TODO*///	}
        /*TODO*///
        /*TODO*///
        /*TODO*///	gone_to_gfx_mode = 1;
        /*TODO*///
        /*TODO*///
        vsync_frame_rate = (int) Machine.drv.frames_per_second;
        /*TODO*///
        /*TODO*///	if (video_sync)
        /*TODO*///	{
        /*TODO*///		TICKER a,b;
        /*TODO*///		float rate;
        /*TODO*///
        /*TODO*///
        /*TODO*///		/* wait some time to let everything stabilize */
        /*TODO*///		for (i = 0;i < 60;i++)
        /*TODO*///		{
        /*TODO*///			vsync();
        /*TODO*///			a = ticker();
        /*TODO*///		}
        /*TODO*///
        /*TODO*///		/* small delay for really really fast machines */
        /*TODO*///		for (i = 0;i < 100000;i++) ;
        /*TODO*///
        /*TODO*///		vsync();
        /*TODO*///		b = ticker();
        /*TODO*///
        /*TODO*///		rate = ((float)TICKS_PER_SEC)/(b-a);
        /*TODO*///
        /*TODO*///		if (errorlog)
        /*TODO*///			fprintf(errorlog,"target frame rate = %ffps, video frame rate = %3.2fHz\n",Machine->drv->frames_per_second,rate);
        /*TODO*///
        /*TODO*///		/* don't allow more than 8% difference between target and actual frame rate */
        /*TODO*///		while (rate > Machine->drv->frames_per_second * 108 / 100)
        /*TODO*///			rate /= 2;
        /*TODO*///
        /*TODO*///		if (rate < Machine->drv->frames_per_second * 92 / 100)
        /*TODO*///		{
        /*TODO*///			osd_close_display();
        /*TODO*///			if (errorlog) fprintf(errorlog,"-vsync option cannot be used with this display mode:\n"
        /*TODO*///						"video refresh frequency = %dHz, target frame rate = %ffps\n",
        /*TODO*///						(int)(TICKS_PER_SEC/(b-a)),Machine->drv->frames_per_second);
        /*TODO*///			return 0;
        /*TODO*///		}
        /*TODO*///
        /*TODO*///		if (errorlog) fprintf(errorlog,"adjusted video frame rate = %3.2fHz\n",rate);
        /*TODO*///			vsync_frame_rate = rate;
        /*TODO*///
        /*TODO*///		if (Machine->sample_rate)
        /*TODO*///		{
        /*TODO*///			Machine->sample_rate = Machine->sample_rate * Machine->drv->frames_per_second / rate;
        /*TODO*///			if (errorlog)
        /*TODO*///				fprintf(errorlog,"sample rate adjusted to match video freq: %d\n",Machine->sample_rate);
        /*TODO*///		}
        /*TODO*///	}
        /*TODO*///
        warming_up = 1;

        /*part of the old arcadeflex_old emulator probably need refactoring */
        Dimension localDimension = Toolkit.getDefaultToolkit().getScreenSize();

        //kill loading window
        osdepend.dlprogress.setVisible(false);
        screen = new software_gfx(settings.version + " (based on mame_old v" + build_version + ")");
        screen.pack();
        //screen.setSize((scanlines==1),gfx_width,gfx_height);//this???
        //screen.setSize((scanlines==1),width,height);//this???
        screen.setSize((scanlines == 0), width, height);
        screen.setBackground(Color.black);
        screen.start();
        screen.run();
        screen.setLocation((int) ((localDimension.getWidth() - screen.getWidth()) / 2.0D), (int) ((localDimension.getHeight() - screen.getHeight()) / 2.0D));
        screen.setVisible(true);
        screen.setResizable((scanlines == 1));

        screen.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                screen.readkey = KeyEvent.VK_ESCAPE;
                screen.key[KeyEvent.VK_ESCAPE] = true;
                osd_refresh();
                if (screen != null) {
                    screen.key[KeyEvent.VK_ESCAPE] = false;
                }
            }
        });

        screen.addComponentListener(new ComponentAdapter() {

            public void componentResized(ComponentEvent evt) {
                screen.resizeVideo();
            }
        });

        screen.addKeyListener(screen);
        screen.setFocusTraversalKeysEnabled(false);
        screen.requestFocus();
        return 1;
    }

    public static void osd_refresh() {
        /*function from old arcadeflex_old */

        if (screen != null) {
            screen.blit();
        }
        try {
            Thread.sleep(100L);
        } catch (InterruptedException localInterruptedException) {
        }
    }

    public static void osd_close_display() {
        /*TODO*///	if (gone_to_gfx_mode != 0)
        /*TODO*///	{
        /*TODO*///		/* tidy up if 15.75KHz SVGA mode used */
        /*TODO*///		if (scanrate15KHz && use_vesa == 1)
        /*TODO*///		{
        /*TODO*///			/* check we've got a valid driver before calling it */
        /*TODO*///			if (SVGA15KHzdriver != NULL)
        /*TODO*///				SVGA15KHzdriver->resetSVGA15KHzmode();
        /*TODO*///		}
        /*TODO*///
        /*TODO*///		set_gfx_mode (GFX_TEXT,0,0,0,0);
        /*TODO*///
        if (frames_displayed > FRAMES_TO_SKIP) {
            printf("Average FPS: %f\n", (double) TICKS_PER_SEC / (end_time - start_time) * (frames_displayed - FRAMES_TO_SKIP));
        }
        /*TODO*///	}
        /*TODO*///
        /*TODO*///	free(dirtycolor);
        /*TODO*///	dirtycolor = 0;
        /*TODO*///	free(current_palette);
        /*TODO*///	current_palette = 0;
        /*TODO*///	free(palette_16bit_lookup);
        /*TODO*///	palette_16bit_lookup = 0;
        /*TODO*///	if (scrbitmap)
        /*TODO*///	{
        /*TODO*///		osd_free_bitmap(scrbitmap);
        /*TODO*///		scrbitmap = NULL;
        /*TODO*///	}
    }

    static int[][] waittable
            = {
                new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                new int[]{2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                new int[]{2, 1, 1, 1, 1, 0, 2, 1, 1, 1, 1, 0},
                new int[]{2, 1, 1, 0, 2, 1, 1, 0, 2, 1, 1, 0},
                new int[]{2, 1, 0, 2, 1, 0, 2, 1, 0, 2, 1, 0},
                new int[]{2, 0, 2, 1, 0, 2, 0, 2, 1, 0, 2, 0},
                new int[]{2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0},
                new int[]{2, 0, 2, 0, 0, 3, 0, 2, 0, 0, 3, 0},
                new int[]{3, 0, 0, 3, 0, 0, 3, 0, 0, 3, 0, 0},
                new int[]{4, 0, 0, 0, 4, 0, 0, 0, 4, 0, 0, 0},
                new int[]{6, 0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0},
                new int[]{12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
            };

    static int showfps, showfpstemp;

    static long prev_measure, this_frame_base, prev;
    static int speed = 100;
    static int vups, vfcount;

    static long last1, last2;
    static int frameskipadjust;

    static long ticksPerFrame, ticksSinceLastFrame; //not in mame_old

    /* Update the display. */
    public static void osd_update_video_and_audio2() {
        long curr;
        int need_to_clear_bitmap = 0;
        int already_synced;

        if (warming_up != 0) {
            /* first time through, initialize timer */
            prev_measure = (ticker() - (long) (FRAMESKIP_LEVELS * TICKS_PER_SEC / Machine.drv.frames_per_second));
            warming_up = 0;
            //use different settings depending on fps (shadow)
            if (Machine.drv.frames_per_second == 30) {
                ticksPerFrame = (long) (TICKS_PER_SEC / Machine.drv.frames_per_second * 0.95);
            }
            if (Machine.drv.frames_per_second == 60) {
                ticksPerFrame = (long) (TICKS_PER_SEC / Machine.drv.frames_per_second * 0.90);
            }
        }

        if (frameskip_counter == 0) {
            this_frame_base = (prev_measure + (long) (FRAMESKIP_LEVELS * TICKS_PER_SEC / Machine.drv.frames_per_second));
        }

        if (throttle != 0) {
            /* if too much time has passed since last sound_old update, disable throttling */
 /* temporarily - we wouldn't be able to keep synch anyway. */
            curr = ticker();
            if ((curr - last1) > (2 * TICKS_PER_SEC / Machine.drv.frames_per_second)) {
                throttle = 1;
            }
            last1 = curr;

            already_synced = 1;//update_audio();

            throttle = 1;
        } else {
            already_synced = 1;//update_audio();
        }
        if (osd_skip_this_frame() == 0) {
            if (showfpstemp != 0) {
                showfpstemp--;
                if (showfps == 0 && showfpstemp == 0) {
                    need_to_clear_bitmap = 1;
                }
            }

            if (input_ui_pressed(IPT_UI_SHOW_FPS) != 0) {
                if (showfpstemp != 0) {
                    showfpstemp = 0;
                    need_to_clear_bitmap = 1;
                } else {
                    showfps ^= 1;
                    if (showfps == 0) {
                        need_to_clear_bitmap = 1;
                    }
                }
            }

            /* now wait until it's time to update the screen */
            //while ((ticker() - ticksSinceLastFrame) < ticksPerFrame)System.Threading.Thread.Sleep(0);
            if (throttle != 0) {
                if (video_sync != 0) {
                    do {
                        //vsync();
                        curr = ticker();
                    } while ((long) (TICKS_PER_SEC / (curr - last2)) > (long) (Machine.drv.frames_per_second * 11L / 10L));

                    last2 = curr;
                } else {
                    long target;
                    /* wait for video sync but use normal throttling */
                    //                        if (wait_vsync != 0)
                    //vsync();
                    while ((ticker() - ticksSinceLastFrame) < ticksPerFrame/**
                             * 0.95
                             */
                            ) ;

                    curr = ticker();

                    if (already_synced == 0) {
                        /* wait only if the audio update hasn't synced us already */

                        target = this_frame_base + (long) (frameskip_counter * TICKS_PER_SEC / Machine.drv.frames_per_second);

                        if (curr - target < 0) {
                            do {
                                curr = ticker();
                            } while (curr - target < 0);
                        }
                    }
                }
            } else {
                curr = ticker();
            }

            /* for the FPS average calculation */
            if (++frames_displayed == FRAMES_TO_SKIP) {
                start_time = curr;
            } else {
                end_time = curr;
            }

            if (frameskip_counter == 0) {
                long divdr = ((int) Machine.drv.frames_per_second * (curr - prev_measure) / (100L * FRAMESKIP_LEVELS));
                speed = (int) ((TICKS_PER_SEC + divdr / 2L) / divdr);
                prev_measure = curr;
            }

            prev = curr;

            vfcount += waittable[frameskip][frameskip_counter];
            if (vfcount >= Machine.drv.frames_per_second) {
                vfcount = 0;
                //    vups = AvgDvg.vector_updates;
                //     AvgDvg.vector_updates = 0;
//                    throw new UnsupportedOperationException("Not supported yet.");
            }

            if (showfps != 0 || showfpstemp != 0) {
                int divdr = 100 * FRAMESKIP_LEVELS;
                int fps = ((int) Machine.drv.frames_per_second * (FRAMESKIP_LEVELS - frameskip) * speed + (divdr / 2)) / divdr;
                String buf = sprintf("%s%2d%4d%%%4d/%d fps", autoframeskip != 0 ? "auto" : "fskp", frameskip, speed, fps, (int) (Machine.drv.frames_per_second + 0.5));
                ui_text(Machine.scrbitmap, buf, Machine.uiwidth - buf.length() * Machine.uifontwidth, 0);
                if (vector_game != 0) {
                    throw new UnsupportedOperationException("Not supported yet.");
                    //buf += sprintf(" %d vector updates", vups);
                    //ui_text(buf, Machine.uiwidth - (buf.Length) * Machine.uifontwidth, Machine.uifontheight);
                }
            }

            if (Machine.scrbitmap.depth == 8) {
                if (dirty_bright != 0) {
                    dirty_bright = 0;
                    for (int i = 0; i < 256; i++) {
                        float rate = (float) (brightness * brightness_paused_adjust * Math.pow(i / 255.0, 1 / osd_gamma_correction) / 100);
                        /*bright_lookup[i] = 63 * rate + 0.5;*/
                        bright_lookup[i] = (int) (255 * rate + 0.5);

                    }
                }
                if (dirtypalette != 0) {
                    dirtypalette = 0;
                    for (int i = 0; i < screen_colors; i++) {
                        if (dirtycolor[i] != 0) {
                            RGB adjusted_palette = new RGB();

                            dirtycolor[i] = 0;

                            adjusted_palette.r = current_palette.read(3 * i + 0);
                            adjusted_palette.g = current_palette.read(3 * i + 1);
                            adjusted_palette.b = current_palette.read(3 * i + 2);
                            if (i != Machine.uifont.colortable.read(1)) /* don't adjust the user interface text */ {
                                adjusted_palette.r = (char) bright_lookup[adjusted_palette.r];
                                adjusted_palette.g = (char) bright_lookup[adjusted_palette.g];
                                adjusted_palette.b = (char) bright_lookup[adjusted_palette.b];
                            } else {

                                /*TODO*///							adjusted_palette.r >>= 2;
                                /*TODO*///							adjusted_palette.g >>= 2;
                                /*TODO*///							adjusted_palette.b >>= 2;
                            }
                            set_color(i, adjusted_palette);
                        }
                    }
                }
            } else {
                throw new UnsupportedOperationException("Not supported yet.");

                /*TODO*///			if (dirty_bright)
                /*TODO*///			{
                /*TODO*///				dirty_bright = 0;
                /*TODO*///				for (i = 0;i < 256;i++)
                /*TODO*///				{
                /*TODO*///					float rate = brightness * brightness_paused_adjust * pow(i / 255.0, 1 / osd_gamma_correction) / 100;
                /*TODO*///					bright_lookup[i] = 255 * rate + 0.5;
                /*TODO*///				}
                /*TODO*///			}
                /*TODO*///			if (dirtypalette)
                /*TODO*///			{
                /*TODO*///				if (use_dirty) init_dirty(1);	/* have to redraw the whole screen */
                /*TODO*///
                /*TODO*///				dirtypalette = 0;
                /*TODO*///				for (i = 0;i < screen_colors;i++)
                /*TODO*///				{
                /*TODO*///					if (dirtycolor[i])
                /*TODO*///					{
                /*TODO*///						int r,g,b;
                /*TODO*///
                /*TODO*///						dirtycolor[i] = 0;
                /*TODO*///
                /*TODO*///						r = current_palette[3*i+0];
                /*TODO*///						g = current_palette[3*i+1];
                /*TODO*///						b = current_palette[3*i+2];
                /*TODO*///						if (i != Machine->uifont->colortable[1])	/* don't adjust the user interface text */
                /*TODO*///						{
                /*TODO*///							r = bright_lookup[r];
                /*TODO*///							g = bright_lookup[g];
                /*TODO*///							b = bright_lookup[b];
                /*TODO*///						}
                /*TODO*///						palette_16bit_lookup[i] = makecol(r,g,b) * 0x10001;
                /*TODO*///					}
                /*TODO*///				}
                /*TODO*///			}
            }

            /* copy the bitmap to screen memory */
            //doupdate_screen();
            blitscreen_dirty1_vga();

            if (need_to_clear_bitmap != 0) {
                osd_clearbitmap(Machine.scrbitmap);
            }

            if (use_dirty != 0) {
                /*TODO*///                   if (!vector_game)
                /*TODO*///                       swap_dirty();
                /*TODO*///                   init_dirty(0);
            }

            if (need_to_clear_bitmap != 0) {
                osd_clearbitmap(Machine.scrbitmap);
            }

            if (throttle == 0 && autoframeskip == 0 && frameskip_counter == 0) {
                /* adjust speed to video refresh rate if vsync is on */
                int adjspeed = (int) (speed * Machine.drv.frames_per_second / vsync_frame_rate);

                if (adjspeed >= 100) {
                    frameskipadjust++;
                    if (frameskipadjust >= 3) {
                        frameskipadjust = 0;
                        if (frameskip > 0) {
                            frameskip--;
                        }
                    }
                } else {
                    if (adjspeed < 80) {
                        frameskipadjust -= (90 - adjspeed) / 5;
                    } else {
                        /* don't push frameskip too far if we are close to 100% speed */
                        if (frameskip < 8) {
                            frameskipadjust--;
                        }
                    }

                    while (frameskipadjust <= -2) {
                        frameskipadjust += 2;
                        if (frameskip < FRAMESKIP_LEVELS - 1) {
                            frameskip++;
                        }
                    }
                }
            }
        }

        /* Check for PGUP, PGDN and pan screen */
        pan_display();

        if (input_ui_pressed(IPT_UI_FRAMESKIP_INC) != 0) {
            if (autoframeskip != 0) {
                autoframeskip = 0;
                frameskip = 0;
            } else {
                if (frameskip == FRAMESKIP_LEVELS - 1) {
                    frameskip = 0;
                    autoframeskip = 1;
                } else {
                    frameskip++;
                }
            }

            if (showfps == 0) {
                showfpstemp = (int) (2 * Machine.drv.frames_per_second);
            }

            /* reset the frame counter every time the frameskip key is pressed, so */
 /* we'll measure the average FPS on a consistent status. */
            frames_displayed = 0;
        }

        if (input_ui_pressed(IPT_UI_FRAMESKIP_DEC) != 0) {
            if (autoframeskip != 0) {
                autoframeskip = 0;
                frameskip = FRAMESKIP_LEVELS - 1;
            } else {
                if (frameskip == 0) {
                    autoframeskip = 1;
                } else {
                    frameskip--;
                }
            }

            if (showfps == 0) {
                showfpstemp = (int) (2 * Machine.drv.frames_per_second);
            }

            /* reset the frame counter every time the frameskip key is pressed, so */
 /* we'll measure the average FPS on a consistent status. */
            frames_displayed = 0;
        }
        if (input_ui_pressed(IPT_UI_THROTTLE) != 0) {
            throttle ^= 1;

            /* reset the frame counter every time the throttle key is pressed, so */
 /* we'll measure the average FPS on a consistent status. */
            frames_displayed = 0;
        }

        frameskip_counter = (frameskip_counter + 1) % FRAMESKIP_LEVELS;
        ticksSinceLastFrame = ticker();
    }

    //TEMP HACK used old arcadeflex_old's sync. should be rewriten to new format
    static final int MEMORY = 10;
    static long[] prev1 = new long[10];
    static int clock_counter;
    static int framecount = 0;

    public static void osd_update_video_and_audio(osd_bitmap bitmap, int led) {
        osd_update_video_and_audio(bitmap);
    }

    public static void osd_update_video_and_audio(osd_bitmap bitmap) {
        if (++framecount > frameskip) {
            framecount = 0;

            if (input_ui_pressed(IPT_UI_SHOW_FPS) != 0) {
                if (showfpstemp != 0) {
                    showfpstemp = 0;
                    need_to_clear_bitmap = 1;
                } else {
                    showfps ^= 1;
                    if (showfps == 0) {
                        need_to_clear_bitmap = 1;
                    }
                }
            }

            long curr;
            /* now wait until it's time to trigger the interrupt */
            do {

                curr = uclock();
            } while ((throttle != 0) && (curr - prev1[clock_counter] < (frameskip + 1) * 1000000000 / Machine.drv.frames_per_second));
            //while (throttle != 0 && video_sync == 0 && (curr - prev[i]) < (frameskip+1) * UCLOCKS_PER_SEC/drv.frames_per_second);
            if (showfps != 0 || showfpstemp != 0) {
                int fps;
                String buf;
                int divdr;

                divdr = 100 * FRAMESKIP_LEVELS;
                fps = ((int) Machine.drv.frames_per_second * (FRAMESKIP_LEVELS - frameskip) * speed + (divdr / 2)) / divdr;
                buf = sprintf("%s%2d%4d%%%4d/%d fps", autoframeskip != 0 ? "auto" : "fskp", frameskip, speed, fps, (int) (Machine.drv.frames_per_second + 0.5));
                ui_text(Machine.scrbitmap, buf, Machine.uiwidth - buf.length() * Machine.uifontwidth, 0);
                if (vector_game != 0) {
                    sprintf(buf, " %d vector updates", vups);
                    ui_text(Machine.scrbitmap, buf, Machine.uiwidth - buf.length() * Machine.uifontwidth, Machine.uifontheight);
                }
            }
            if (Machine.scrbitmap.depth == 8) {
                if (dirty_bright != 0) {
                    dirty_bright = 0;
                    for (int i = 0; i < 256; i++) {
                        float rate = (float) (brightness * brightness_paused_adjust * Math.pow(i / 255.0, 1 / osd_gamma_correction) / 100);
                        /*bright_lookup[i] = 63 * rate + 0.5;*/
                        bright_lookup[i] = (int) (255 * rate + 0.5);

                    }
                }
                if (dirtypalette != 0) {
                    dirtypalette = 0;
                    for (int i = 0; i < screen_colors; i++) {
                        if (dirtycolor[i] != 0) {
                            RGB adjusted_palette = new RGB();

                            dirtycolor[i] = 0;

                            adjusted_palette.r = current_palette.read(3 * i + 0);
                            adjusted_palette.g = current_palette.read(3 * i + 1);
                            adjusted_palette.b = current_palette.read(3 * i + 2);
                            if (i != Machine.uifont.colortable.read(1)) /* don't adjust the user interface text */ {
                                adjusted_palette.r = (char) bright_lookup[adjusted_palette.r];
                                adjusted_palette.g = (char) bright_lookup[adjusted_palette.g];
                                adjusted_palette.b = (char) bright_lookup[adjusted_palette.b];
                            } else {

                                /*TODO*///							adjusted_palette.r >>= 2;
                                /*TODO*///							adjusted_palette.g >>= 2;
                                /*TODO*///							adjusted_palette.b >>= 2;
                            }
                            set_color(i, adjusted_palette);
                        }
                    }
                }
            }
            /*TODO*///		else
            /*TODO*///		{
            /*TODO*///			if (dirty_bright)
            /*TODO*///			{
            /*TODO*///				dirty_bright = 0;
            /*TODO*///				for (i = 0;i < 256;i++)
            /*TODO*///				{
            /*TODO*///					float rate = brightness * brightness_paused_adjust * pow(i / 255.0, 1 / osd_gamma_correction) / 100;
            /*TODO*///					bright_lookup[i] = 255 * rate + 0.5;
            /*TODO*///				}
            /*TODO*///			}
            /*TODO*///			if (dirtypalette)
            /*TODO*///			{
            /*TODO*///				if (use_dirty) init_dirty(1);	/* have to redraw the whole screen */
            /*TODO*///
            /*TODO*///				dirtypalette = 0;
            /*TODO*///				for (i = 0;i < screen_colors;i++)
            /*TODO*///				{
            /*TODO*///					if (dirtycolor[i])
            /*TODO*///					{
            /*TODO*///						int r,g,b;
            /*TODO*///
            /*TODO*///						dirtycolor[i] = 0;
            /*TODO*///
            /*TODO*///						r = current_palette[3*i+0];
            /*TODO*///						g = current_palette[3*i+1];
            /*TODO*///						b = current_palette[3*i+2];
            /*TODO*///						if (i != Machine->uifont->colortable[1])	/* don't adjust the user interface text */
            /*TODO*///						{
            /*TODO*///							r = bright_lookup[r];
            /*TODO*///							g = bright_lookup[g];
            /*TODO*///							b = bright_lookup[b];
            /*TODO*///						}
            /*TODO*///						palette_16bit_lookup[i] = makecol(r,g,b) * 0x10001;
            /*TODO*///					}
            /*TODO*///				}
            /*TODO*///			}
            /*TODO*///		}
            blitscreen_dirty1_vga();
            update_audio();
            if (need_to_clear_bitmap != 0) {
                osd_clearbitmap(Machine.scrbitmap);
            }
            clock_counter = (clock_counter + 1) % MEMORY;
            if ((curr - prev1[clock_counter]) != 0) {
                long divdr = (int) Machine.drv.frames_per_second * (curr - prev1[clock_counter]) / (100L * MEMORY);

                speed = (int) ((UCLOCKS_PER_SEC * (frameskip + 1) + divdr / 2L) / divdr);
            }

            prev1[clock_counter] = curr;
        }
    }

}
