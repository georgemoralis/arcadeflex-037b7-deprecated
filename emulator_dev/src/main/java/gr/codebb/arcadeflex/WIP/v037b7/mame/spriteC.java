/*
 * ported to 0.37b7 
*/
package gr.codebb.arcadeflex.WIP.v037b7.mame;

public class spriteC {

    /*TODO*///#define SWAP(X,Y) { int temp = X; X = Y; Y = temp; }
/*TODO*///
/*TODO*///
/*TODO*///static int orientation, screen_width, screen_height;
/*TODO*///static int screen_clip_left, screen_clip_top, screen_clip_right, screen_clip_bottom;
/*TODO*///unsigned char *screen_baseaddr;
/*TODO*///int screen_line_offset;
/*TODO*///
/*TODO*///static struct sprite_list *first_sprite_list = NULL; /* used for resource tracking */
/*TODO*///static int FlickeringInvisible;
/*TODO*///
/*TODO*///static UINT16 *shade_table;
/*TODO*///
/*TODO*///static void sprite_order_setup( struct sprite_list *sprite_list, int *first, int *last, int *delta ){
/*TODO*///	if( sprite_list->flags&SPRITE_LIST_FRONT_TO_BACK ){
/*TODO*///		*delta = -1;
/*TODO*///		*first = sprite_list->num_sprites-1;
/*TODO*///		*last = 0;
/*TODO*///	}
/*TODO*///	else {
/*TODO*///		*delta = 1;
/*TODO*///		*first = 0;
/*TODO*///		*last = sprite_list->num_sprites-1;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*////*********************************************************************
/*TODO*///
/*TODO*///	The mask buffer is a dynamically allocated resource
/*TODO*///	it is recycled each frame.  Using this technique reduced the runttime
/*TODO*///	memory requirements of the Gaiden from 512k (worst case) to approx 6K.
/*TODO*///
/*TODO*///	Sprites use offsets instead of pointers directly to the mask data, since it
/*TODO*///	is potentially reallocated.
/*TODO*///
/*TODO*///*********************************************************************/
/*TODO*///static unsigned char *mask_buffer = NULL;
/*TODO*///static int mask_buffer_size = 0; /* actual size of allocated buffer */
/*TODO*///static int mask_buffer_used = 0;
/*TODO*///
/*TODO*///static void mask_buffer_reset( void ){
/*TODO*///	mask_buffer_used = 0;
/*TODO*///}
/*TODO*///static void mask_buffer_dispose( void ){
/*TODO*///	free( mask_buffer );
/*TODO*///	mask_buffer = NULL;
/*TODO*///	mask_buffer_size = 0;
/*TODO*///}
/*TODO*///static long mask_buffer_alloc( long size ){
/*TODO*///	long result = mask_buffer_used;
/*TODO*///	long req_size = mask_buffer_used + size;
/*TODO*///	if( req_size>mask_buffer_size ){
/*TODO*///		mask_buffer = realloc( mask_buffer, req_size );
/*TODO*///		mask_buffer_size = req_size;
/*TODO*///		logerror("increased sprite mask buffer size to %d bytes.\n", mask_buffer_size );
/*TODO*///		if( !mask_buffer ) logerror("Error! insufficient memory for mask_buffer_alloc\n" );
/*TODO*///	}
/*TODO*///	mask_buffer_used = req_size;
/*TODO*///	memset( &mask_buffer[result], 0x00, size ); /* clear it */
/*TODO*///	return result;
/*TODO*///}
/*TODO*///
/*TODO*///#define BLIT \
/*TODO*///if( sprite->flags&SPRITE_FLIPX ){ \
/*TODO*///	source += screenx + flipx_adjust; \
/*TODO*///	for( y=y1; y<y2; y++ ){ \
/*TODO*///		for( x=x1; x<x2; x++ ){ \
/*TODO*///			if( OPAQUE(-x) ) dest[x] = COLOR(-x); \
/*TODO*///		} \
/*TODO*///		source += source_dy; dest += blit.line_offset; \
/*TODO*///		NEXTLINE \
/*TODO*///	} \
/*TODO*///} \
/*TODO*///else { \
/*TODO*///	source -= screenx; \
/*TODO*///	for( y=y1; y<y2; y++ ){ \
/*TODO*///		for( x=x1; x<x2; x++ ){ \
/*TODO*///			if( OPAQUE(x) ) dest[x] = COLOR(x); \
/*TODO*///			\
/*TODO*///		} \
/*TODO*///		source += source_dy; dest += blit.line_offset; \
/*TODO*///		NEXTLINE \
/*TODO*///	} \
/*TODO*///}
/*TODO*///
/*TODO*///static struct {
/*TODO*///	int transparent_pen;
/*TODO*///	int clip_left, clip_right, clip_top, clip_bottom;
/*TODO*///	unsigned char *baseaddr;
/*TODO*///	int line_offset;
/*TODO*///	int write_to_mask;
/*TODO*///	int origin_x, origin_y;
/*TODO*///} blit;
/*TODO*///
/*TODO*///static void do_blit_unpack( const struct sprite *sprite ){
/*TODO*///	const unsigned short *pal_data = sprite->pal_data;
/*TODO*///	int transparent_pen = blit.transparent_pen;
/*TODO*///
/*TODO*///	int screenx = sprite->x - blit.origin_x;
/*TODO*///	int screeny = sprite->y - blit.origin_y;
/*TODO*///	int x1 = screenx;
/*TODO*///	int y1 = screeny;
/*TODO*///	int x2 = x1 + sprite->total_width;
/*TODO*///	int y2 = y1 + sprite->total_height;
/*TODO*///	int flipx_adjust = sprite->total_width-1;
/*TODO*///
/*TODO*///	int source_dy;
/*TODO*///	const unsigned char *baseaddr = sprite->pen_data;
/*TODO*///	const unsigned char *source;
/*TODO*///	unsigned char *dest;
/*TODO*///	int x,y;
/*TODO*///
/*TODO*///	source = baseaddr + sprite->line_offset*sprite->y_offset + sprite->x_offset;
/*TODO*///
/*TODO*///	if( x1<blit.clip_left )		x1 = blit.clip_left;
/*TODO*///	if( y1<blit.clip_top )		y1 = blit.clip_top;
/*TODO*///	if( x2>blit.clip_right )	x2 = blit.clip_right;
/*TODO*///	if( y2>blit.clip_bottom )	y2 = blit.clip_bottom;
/*TODO*///
/*TODO*///	if( x1<x2 && y1<y2 ){
/*TODO*///		dest = blit.baseaddr + y1*blit.line_offset;
/*TODO*///		if( sprite->flags&SPRITE_FLIPY ){
/*TODO*///			source_dy = -sprite->line_offset;
/*TODO*///			source += (y2-1-screeny)*sprite->line_offset;
/*TODO*///		}
/*TODO*///		else {
/*TODO*///			source_dy = sprite->line_offset;
/*TODO*///			source += (y1-screeny)*sprite->line_offset;
/*TODO*///		}
/*TODO*///		if( blit.write_to_mask ){
/*TODO*///			#define OPAQUE(X) (source[X]!=transparent_pen)
/*TODO*///			#define COLOR(X) 0xff
/*TODO*///			#define NEXTLINE
/*TODO*///			BLIT
/*TODO*///			#undef OPAQUE
/*TODO*///			#undef COLOR
/*TODO*///			#undef NEXTLINE
/*TODO*///		}
/*TODO*///		else if( sprite->mask_offset>=0 ){ /* draw a masked sprite */
/*TODO*///			const unsigned char *mask = &mask_buffer[sprite->mask_offset] +
/*TODO*///				(y1-sprite->y)*sprite->total_width-sprite->x;
/*TODO*///			#define OPAQUE(X) (mask[x]==0 && source[X]!=transparent_pen)
/*TODO*///			#define COLOR(X) (pal_data[source[X]])
/*TODO*///			#define NEXTLINE mask+=sprite->total_width;
/*TODO*///			BLIT
/*TODO*///			#undef OPAQUE
/*TODO*///			#undef COLOR
/*TODO*///			#undef NEXTLINE
/*TODO*///		}
/*TODO*///		else if( sprite->flags&SPRITE_TRANSPARENCY_THROUGH ){
/*TODO*///			int color = Machine->pens[palette_transparent_pen];
/*TODO*///			#define OPAQUE(X) (dest[x]==color && source[X]!=transparent_pen)
/*TODO*///			#define COLOR(X) (pal_data[source[X]])
/*TODO*///			#define NEXTLINE
/*TODO*///			BLIT
/*TODO*///			#undef OPAQUE
/*TODO*///			#undef COLOR
/*TODO*///			#undef NEXTLINE
/*TODO*///		}
/*TODO*///		else if( pal_data ){
/*TODO*///			#define OPAQUE(X) (source[X]!=transparent_pen)
/*TODO*///			#define COLOR(X) (pal_data[source[X]])
/*TODO*///			#define NEXTLINE
/*TODO*///			BLIT
/*TODO*///			#undef OPAQUE
/*TODO*///			#undef COLOR
/*TODO*///			#undef NEXTLINE
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///static void do_blit_stack( const struct sprite *sprite ){
/*TODO*///	const unsigned short *pal_data = sprite->pal_data;
/*TODO*///	int transparent_pen = blit.transparent_pen;
/*TODO*///	int flipx_adjust = sprite->tile_width-1;
/*TODO*///
/*TODO*///	int xoffset, yoffset;
/*TODO*///	int screenx, screeny;
/*TODO*///	int x1, y1, x2, y2;
/*TODO*///	int x,y;
/*TODO*///
/*TODO*///	int source_dy;
/*TODO*///	const unsigned char *baseaddr = sprite->pen_data;
/*TODO*///	const unsigned char *source;
/*TODO*///	unsigned char *dest;
/*TODO*///
/*TODO*///	for( xoffset =0; xoffset<sprite->total_width; xoffset+=sprite->tile_width ){
/*TODO*///		for( yoffset=0; yoffset<sprite->total_height; yoffset+=sprite->tile_height ){
/*TODO*///			source = baseaddr;
/*TODO*///			screenx = sprite->x - blit.origin_x;
/*TODO*///			screeny = sprite->y - blit.origin_y;
/*TODO*///
/*TODO*///			if( sprite->flags & SPRITE_FLIPX ){
/*TODO*///				screenx += sprite->total_width - sprite->tile_width - xoffset;
/*TODO*///			}
/*TODO*///			else {
/*TODO*///				screenx += xoffset;
/*TODO*///			}
/*TODO*///
/*TODO*///			if( sprite->flags & SPRITE_FLIPY ){
/*TODO*///				screeny += sprite->total_height - sprite->tile_height - yoffset;
/*TODO*///			}
/*TODO*///			else {
/*TODO*///				screeny += yoffset;
/*TODO*///			}
/*TODO*///
/*TODO*///			x1 = screenx;
/*TODO*///			y1 = screeny;
/*TODO*///			x2 = x1 + sprite->tile_width;
/*TODO*///			y2 = y1 + sprite->tile_height;
/*TODO*///
/*TODO*///			if( x1<blit.clip_left )		x1 = blit.clip_left;
/*TODO*///			if( y1<blit.clip_top )		y1 = blit.clip_top;
/*TODO*///			if( x2>blit.clip_right )	x2 = blit.clip_right;
/*TODO*///			if( y2>blit.clip_bottom )	y2 = blit.clip_bottom;
/*TODO*///
/*TODO*///			if( x1<x2 && y1<y2 ){
/*TODO*///				dest = blit.baseaddr + y1*blit.line_offset;
/*TODO*///
/*TODO*///				if( sprite->flags&SPRITE_FLIPY ){
/*TODO*///					source_dy = -sprite->line_offset;
/*TODO*///					source += (y2-1-screeny)*sprite->line_offset;
/*TODO*///				}
/*TODO*///				else {
/*TODO*///					source_dy = sprite->line_offset;
/*TODO*///					source += (y1-screeny)*sprite->line_offset;
/*TODO*///				}
/*TODO*///
/*TODO*///				if( blit.write_to_mask ){
/*TODO*///					#define OPAQUE(X) (source[X]!=transparent_pen)
/*TODO*///					#define COLOR(X) 0xff
/*TODO*///					#define NEXTLINE
/*TODO*///					BLIT
/*TODO*///					#undef OPAQUE
/*TODO*///					#undef COLOR
/*TODO*///					#undef NEXTLINE
/*TODO*///				}
/*TODO*///				else if( sprite->mask_offset>=0 ){ /* draw a masked sprite */
/*TODO*///					const unsigned char *mask = &mask_buffer[sprite->mask_offset] +
/*TODO*///						(y1-sprite->y)*sprite->total_width-sprite->x;
/*TODO*///					#define OPAQUE(X) (mask[x]==0 && source[X]!=transparent_pen)
/*TODO*///					#define COLOR(X) (pal_data[source[X]])
/*TODO*///					#define NEXTLINE mask+=sprite->total_width;
/*TODO*///					BLIT
/*TODO*///					#undef OPAQUE
/*TODO*///					#undef COLOR
/*TODO*///					#undef NEXTLINE
/*TODO*///				}
/*TODO*///				else if( sprite->flags&SPRITE_TRANSPARENCY_THROUGH ){
/*TODO*///					int color = Machine->pens[palette_transparent_pen];
/*TODO*///					#define OPAQUE(X) (dest[x]==color && source[X]!=transparent_pen)
/*TODO*///					#define COLOR(X) (pal_data[source[X]])
/*TODO*///					#define NEXTLINE
/*TODO*///					BLIT
/*TODO*///					#undef OPAQUE
/*TODO*///					#undef COLOR
/*TODO*///					#undef NEXTLINE
/*TODO*///				}
/*TODO*///				else if( pal_data ){
/*TODO*///					#define OPAQUE(X) (source[X]!=transparent_pen)
/*TODO*///					#define COLOR(X) (pal_data[source[X]])
/*TODO*///					#define NEXTLINE
/*TODO*///					BLIT
/*TODO*///					#undef OPAQUE
/*TODO*///					#undef COLOR
/*TODO*///					#undef NEXTLINE
/*TODO*///				}
/*TODO*///			} /* not totally clipped */
/*TODO*///			baseaddr += sprite->tile_height*sprite->line_offset;
/*TODO*///		} /* next yoffset */
/*TODO*///	} /* next xoffset */
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///
/*TODO*///static void do_blit_zoom( const struct sprite *sprite ){
/*TODO*///	/*	assumes SPRITE_LIST_RAW_DATA flag is set */
/*TODO*///
/*TODO*///	int x1,x2, y1,y2, dx,dy;
/*TODO*///	int xcount0 = 0, ycount0 = 0;
/*TODO*///
/*TODO*///	if( sprite->flags & SPRITE_FLIPX ){
/*TODO*///		x2 = sprite->x;
/*TODO*///		x1 = x2+sprite->total_width;
/*TODO*///		dx = -1;
/*TODO*///		if( x2<blit.clip_left ) x2 = blit.clip_left;
/*TODO*///		if( x1>blit.clip_right ){
/*TODO*///			xcount0 = (x1-blit.clip_right)*sprite->tile_width;
/*TODO*///			x1 = blit.clip_right;
/*TODO*///		}
/*TODO*///		if( x2>=x1 ) return;
/*TODO*///		x1--; x2--;
/*TODO*///	}
/*TODO*///	else {
/*TODO*///		x1 = sprite->x;
/*TODO*///		x2 = x1+sprite->total_width;
/*TODO*///		dx = 1;
/*TODO*///		if( x1<blit.clip_left ){
/*TODO*///			xcount0 = (blit.clip_left-x1)*sprite->tile_width;
/*TODO*///			x1 = blit.clip_left;
/*TODO*///		}
/*TODO*///		if( x2>blit.clip_right ) x2 = blit.clip_right;
/*TODO*///		if( x1>=x2 ) return;
/*TODO*///	}
/*TODO*///	if( sprite->flags & SPRITE_FLIPY ){
/*TODO*///		y2 = sprite->y;
/*TODO*///		y1 = y2+sprite->total_height;
/*TODO*///		dy = -1;
/*TODO*///		if( y2<blit.clip_top ) y2 = blit.clip_top;
/*TODO*///		if( y1>blit.clip_bottom ){
/*TODO*///			ycount0 = (y1-blit.clip_bottom)*sprite->tile_height;
/*TODO*///			y1 = blit.clip_bottom;
/*TODO*///		}
/*TODO*///		if( y2>=y1 ) return;
/*TODO*///		y1--; y2--;
/*TODO*///	}
/*TODO*///	else {
/*TODO*///		y1 = sprite->y;
/*TODO*///		y2 = y1+sprite->total_height;
/*TODO*///		dy = 1;
/*TODO*///		if( y1<blit.clip_top ){
/*TODO*///			ycount0 = (blit.clip_top-y1)*sprite->tile_height;
/*TODO*///			y1 = blit.clip_top;
/*TODO*///		}
/*TODO*///		if( y2>blit.clip_bottom ) y2 = blit.clip_bottom;
/*TODO*///		if( y1>=y2 ) return;
/*TODO*///	}
/*TODO*///
/*TODO*///	if(!(sprite->flags & (SPRITE_SHADOW | SPRITE_PARTIAL_SHADOW)))
/*TODO*///	{
/*TODO*///		const unsigned char *pen_data = sprite->pen_data;
/*TODO*///		const unsigned short *pal_data = sprite->pal_data;
/*TODO*///		int x,y;
/*TODO*///		unsigned char pen;
/*TODO*///		int pitch = blit.line_offset*dy;
/*TODO*///		unsigned char *dest = blit.baseaddr + blit.line_offset*y1;
/*TODO*///		int ycount = ycount0;
/*TODO*///
/*TODO*///		if( orientation & ORIENTATION_SWAP_XY ){ /* manually rotate the sprite graphics */
/*TODO*///			int xcount = xcount0;
/*TODO*///			for( x=x1; x!=x2; x+=dx ){
/*TODO*///				const unsigned char *source;
/*TODO*///				unsigned char *dest1;
/*TODO*///
/*TODO*///				ycount = ycount0;
/*TODO*///				while( xcount>=sprite->total_width ){
/*TODO*///					xcount -= sprite->total_width;
/*TODO*///					pen_data+=sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				dest1 = &dest[x];
/*TODO*///				for( y=y1; y!=y2; y+=dy ){
/*TODO*///					while( ycount>=sprite->total_height ){
/*TODO*///						ycount -= sprite->total_height;
/*TODO*///						source ++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip1; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*////*					if( pen==10 ) *dest1 = shade_table[*dest1];
/*TODO*///					else */if( pen ) *dest1 = pal_data[pen];
/*TODO*///					ycount+= sprite->tile_height;
/*TODO*///					dest1 += pitch;
/*TODO*///				}
/*TODO*///skip1:
/*TODO*///				xcount += sprite->tile_width;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else {
/*TODO*///			for( y=y1; y!=y2; y+=dy ){
/*TODO*///				int xcount = xcount0;
/*TODO*///				const unsigned char *source;
/*TODO*///				while( ycount>=sprite->total_height ){
/*TODO*///					ycount -= sprite->total_height;
/*TODO*///					pen_data += sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				for( x=x1; x!=x2; x+=dx ){
/*TODO*///					while( xcount>=sprite->total_width ){
/*TODO*///						xcount -= sprite->total_width;
/*TODO*///						source++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*////*					if( pen==10 ) dest[x] = shade_table[dest[x]];
/*TODO*///					else */if( pen ) dest[x] = pal_data[pen];
/*TODO*///					xcount += sprite->tile_width;
/*TODO*///				}
/*TODO*///skip:
/*TODO*///				ycount += sprite->tile_height;
/*TODO*///				dest += pitch;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else if(sprite->flags & SPRITE_PARTIAL_SHADOW)
/*TODO*///	{
/*TODO*///		const unsigned char *pen_data = sprite->pen_data;
/*TODO*///		const unsigned short *pal_data = sprite->pal_data;
/*TODO*///		int x,y;
/*TODO*///		unsigned char pen;
/*TODO*///		int pitch = blit.line_offset*dy;
/*TODO*///		unsigned char *dest = blit.baseaddr + blit.line_offset*y1;
/*TODO*///		int ycount = ycount0;
/*TODO*///
/*TODO*///		if( orientation & ORIENTATION_SWAP_XY ){ /* manually rotate the sprite graphics */
/*TODO*///			int xcount = xcount0;
/*TODO*///			for( x=x1; x!=x2; x+=dx ){
/*TODO*///				const unsigned char *source;
/*TODO*///				unsigned char *dest1;
/*TODO*///
/*TODO*///				ycount = ycount0;
/*TODO*///				while( xcount>=sprite->total_width ){
/*TODO*///					xcount -= sprite->total_width;
/*TODO*///					pen_data+=sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				dest1 = &dest[x];
/*TODO*///				for( y=y1; y!=y2; y+=dy ){
/*TODO*///					while( ycount>=sprite->total_height ){
/*TODO*///						ycount -= sprite->total_height;
/*TODO*///						source ++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip6; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen==sprite->shadow_pen ) *dest1 = shade_table[*dest1];
/*TODO*///					else if( pen ) *dest1 = pal_data[pen];
/*TODO*///					ycount+= sprite->tile_height;
/*TODO*///					dest1 += pitch;
/*TODO*///				}
/*TODO*///skip6:
/*TODO*///				xcount += sprite->tile_width;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else {
/*TODO*///			for( y=y1; y!=y2; y+=dy ){
/*TODO*///				int xcount = xcount0;
/*TODO*///				const unsigned char *source;
/*TODO*///				while( ycount>=sprite->total_height ){
/*TODO*///					ycount -= sprite->total_height;
/*TODO*///					pen_data += sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				for( x=x1; x!=x2; x+=dx ){
/*TODO*///					while( xcount>=sprite->total_width ){
/*TODO*///						xcount -= sprite->total_width;
/*TODO*///						source++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip5; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen==sprite->shadow_pen ) dest[x] = shade_table[dest[x]];
/*TODO*///					else if( pen ) dest[x] = pal_data[pen];
/*TODO*///					xcount += sprite->tile_width;
/*TODO*///				}
/*TODO*///skip5:
/*TODO*///				ycount += sprite->tile_height;
/*TODO*///				dest += pitch;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{	// Shadow Sprite
/*TODO*///		const unsigned char *pen_data = sprite->pen_data;
/*TODO*/////		const unsigned short *pal_data = sprite->pal_data;
/*TODO*///		int x,y;
/*TODO*///		unsigned char pen;
/*TODO*///		int pitch = blit.line_offset*dy;
/*TODO*///		unsigned char *dest = blit.baseaddr + blit.line_offset*y1;
/*TODO*///		int ycount = ycount0;
/*TODO*///
/*TODO*///		if( orientation & ORIENTATION_SWAP_XY ){ /* manually rotate the sprite graphics */
/*TODO*///			int xcount = xcount0;
/*TODO*///			for( x=x1; x!=x2; x+=dx ){
/*TODO*///				const unsigned char *source;
/*TODO*///				unsigned char *dest1;
/*TODO*///
/*TODO*///				ycount = ycount0;
/*TODO*///				while( xcount>=sprite->total_width ){
/*TODO*///					xcount -= sprite->total_width;
/*TODO*///					pen_data+=sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				dest1 = &dest[x];
/*TODO*///				for( y=y1; y!=y2; y+=dy ){
/*TODO*///					while( ycount>=sprite->total_height ){
/*TODO*///						ycount -= sprite->total_height;
/*TODO*///						source ++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip4; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen ) *dest1 = shade_table[*dest1];
/*TODO*///					ycount+= sprite->tile_height;
/*TODO*///					dest1 += pitch;
/*TODO*///				}
/*TODO*///skip4:
/*TODO*///				xcount += sprite->tile_width;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else {
/*TODO*///			for( y=y1; y!=y2; y+=dy ){
/*TODO*///				int xcount = xcount0;
/*TODO*///				const unsigned char *source;
/*TODO*///				while( ycount>=sprite->total_height ){
/*TODO*///					ycount -= sprite->total_height;
/*TODO*///					pen_data += sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				for( x=x1; x!=x2; x+=dx ){
/*TODO*///					while( xcount>=sprite->total_width ){
/*TODO*///						xcount -= sprite->total_width;
/*TODO*///						source++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip3; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen ) dest[x] = shade_table[dest[x]];
/*TODO*///					xcount += sprite->tile_width;
/*TODO*///				}
/*TODO*///skip3:
/*TODO*///				ycount += sprite->tile_height;
/*TODO*///				dest += pitch;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///static void do_blit_zoom16( const struct sprite *sprite ){
/*TODO*///	/*	assumes SPRITE_LIST_RAW_DATA flag is set */
/*TODO*///
/*TODO*///	int x1,x2, y1,y2, dx,dy;
/*TODO*///	int xcount0 = 0, ycount0 = 0;
/*TODO*///
/*TODO*///	if( sprite->flags & SPRITE_FLIPX ){
/*TODO*///		x2 = sprite->x;
/*TODO*///		x1 = x2+sprite->total_width;
/*TODO*///		dx = -1;
/*TODO*///		if( x2<blit.clip_left ) x2 = blit.clip_left;
/*TODO*///		if( x1>blit.clip_right ){
/*TODO*///			xcount0 = (x1-blit.clip_right)*sprite->tile_width;
/*TODO*///			x1 = blit.clip_right;
/*TODO*///		}
/*TODO*///		if( x2>=x1 ) return;
/*TODO*///		x1--; x2--;
/*TODO*///	}
/*TODO*///	else {
/*TODO*///		x1 = sprite->x;
/*TODO*///		x2 = x1+sprite->total_width;
/*TODO*///		dx = 1;
/*TODO*///		if( x1<blit.clip_left ){
/*TODO*///			xcount0 = (blit.clip_left-x1)*sprite->tile_width;
/*TODO*///			x1 = blit.clip_left;
/*TODO*///		}
/*TODO*///		if( x2>blit.clip_right ) x2 = blit.clip_right;
/*TODO*///		if( x1>=x2 ) return;
/*TODO*///	}
/*TODO*///	if( sprite->flags & SPRITE_FLIPY ){
/*TODO*///		y2 = sprite->y;
/*TODO*///		y1 = y2+sprite->total_height;
/*TODO*///		dy = -1;
/*TODO*///		if( y2<blit.clip_top ) y2 = blit.clip_top;
/*TODO*///		if( y1>blit.clip_bottom ){
/*TODO*///			ycount0 = (y1-blit.clip_bottom)*sprite->tile_height;
/*TODO*///			y1 = blit.clip_bottom;
/*TODO*///		}
/*TODO*///		if( y2>=y1 ) return;
/*TODO*///		y1--; y2--;
/*TODO*///	}
/*TODO*///	else {
/*TODO*///		y1 = sprite->y;
/*TODO*///		y2 = y1+sprite->total_height;
/*TODO*///		dy = 1;
/*TODO*///		if( y1<blit.clip_top ){
/*TODO*///			ycount0 = (blit.clip_top-y1)*sprite->tile_height;
/*TODO*///			y1 = blit.clip_top;
/*TODO*///		}
/*TODO*///		if( y2>blit.clip_bottom ) y2 = blit.clip_bottom;
/*TODO*///		if( y1>=y2 ) return;
/*TODO*///	}
/*TODO*///
/*TODO*///	if(!(sprite->flags & (SPRITE_SHADOW | SPRITE_PARTIAL_SHADOW)))
/*TODO*///	{
/*TODO*///		const unsigned char *pen_data = sprite->pen_data;
/*TODO*///		const unsigned short *pal_data = sprite->pal_data;
/*TODO*///		int x,y;
/*TODO*///		unsigned char pen;
/*TODO*///		int pitch = blit.line_offset*dy/2;
/*TODO*///		UINT16 *dest = (UINT16 *)(blit.baseaddr + blit.line_offset*y1);
/*TODO*///		int ycount = ycount0;
/*TODO*///
/*TODO*///		if( orientation & ORIENTATION_SWAP_XY ){ /* manually rotate the sprite graphics */
/*TODO*///			int xcount = xcount0;
/*TODO*///			for( x=x1; x!=x2; x+=dx ){
/*TODO*///				const unsigned char *source;
/*TODO*///				UINT16 *dest1;
/*TODO*///
/*TODO*///				ycount = ycount0;
/*TODO*///				while( xcount>=sprite->total_width ){
/*TODO*///					xcount -= sprite->total_width;
/*TODO*///					pen_data+=sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				dest1 = &dest[x];
/*TODO*///				for( y=y1; y!=y2; y+=dy ){
/*TODO*///					while( ycount>=sprite->total_height ){
/*TODO*///						ycount -= sprite->total_height;
/*TODO*///						source ++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip1; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*////*					if( pen==10 ) *dest1 = shade_table[*dest1];
/*TODO*///					else */if( pen ) *dest1 = pal_data[pen];
/*TODO*///					ycount+= sprite->tile_height;
/*TODO*///					dest1 += pitch;
/*TODO*///				}
/*TODO*///skip1:
/*TODO*///				xcount += sprite->tile_width;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else {
/*TODO*///			for( y=y1; y!=y2; y+=dy ){
/*TODO*///				int xcount = xcount0;
/*TODO*///				const unsigned char *source;
/*TODO*///				while( ycount>=sprite->total_height ){
/*TODO*///					ycount -= sprite->total_height;
/*TODO*///					pen_data += sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				for( x=x1; x!=x2; x+=dx ){
/*TODO*///					while( xcount>=sprite->total_width ){
/*TODO*///						xcount -= sprite->total_width;
/*TODO*///						source++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*////*					if( pen==10 ) dest[x] = shade_table[dest[x]];
/*TODO*///					else */if( pen ) dest[x] = pal_data[pen];
/*TODO*///					xcount += sprite->tile_width;
/*TODO*///				}
/*TODO*///skip:
/*TODO*///				ycount += sprite->tile_height;
/*TODO*///				dest += pitch;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else if(sprite->flags & SPRITE_PARTIAL_SHADOW)
/*TODO*///	{
/*TODO*///		const unsigned char *pen_data = sprite->pen_data;
/*TODO*///		const unsigned short *pal_data = sprite->pal_data;
/*TODO*///		int x,y;
/*TODO*///		unsigned char pen;
/*TODO*///		int pitch = blit.line_offset*dy/2;
/*TODO*///		UINT16 *dest = (UINT16 *)(blit.baseaddr + blit.line_offset*y1);
/*TODO*///		int ycount = ycount0;
/*TODO*///
/*TODO*///		if( orientation & ORIENTATION_SWAP_XY ){ /* manually rotate the sprite graphics */
/*TODO*///			int xcount = xcount0;
/*TODO*///			for( x=x1; x!=x2; x+=dx ){
/*TODO*///				const unsigned char *source;
/*TODO*///				UINT16 *dest1;
/*TODO*///
/*TODO*///				ycount = ycount0;
/*TODO*///				while( xcount>=sprite->total_width ){
/*TODO*///					xcount -= sprite->total_width;
/*TODO*///					pen_data+=sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				dest1 = &dest[x];
/*TODO*///				for( y=y1; y!=y2; y+=dy ){
/*TODO*///					while( ycount>=sprite->total_height ){
/*TODO*///						ycount -= sprite->total_height;
/*TODO*///						source ++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip6; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen==sprite->shadow_pen ) *dest1 = shade_table[*dest1];
/*TODO*///					else if( pen ) *dest1 = pal_data[pen];
/*TODO*///					ycount+= sprite->tile_height;
/*TODO*///					dest1 += pitch;
/*TODO*///				}
/*TODO*///skip6:
/*TODO*///				xcount += sprite->tile_width;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else {
/*TODO*///			for( y=y1; y!=y2; y+=dy ){
/*TODO*///				int xcount = xcount0;
/*TODO*///				const unsigned char *source;
/*TODO*///				while( ycount>=sprite->total_height ){
/*TODO*///					ycount -= sprite->total_height;
/*TODO*///					pen_data += sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				for( x=x1; x!=x2; x+=dx ){
/*TODO*///					while( xcount>=sprite->total_width ){
/*TODO*///						xcount -= sprite->total_width;
/*TODO*///						source++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip5; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen==sprite->shadow_pen ) dest[x] = shade_table[dest[x]];
/*TODO*///					else if( pen ) dest[x] = pal_data[pen];
/*TODO*///					xcount += sprite->tile_width;
/*TODO*///				}
/*TODO*///skip5:
/*TODO*///				ycount += sprite->tile_height;
/*TODO*///				dest += pitch;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///	else
/*TODO*///	{	// Shadow Sprite
/*TODO*///		const unsigned char *pen_data = sprite->pen_data;
/*TODO*/////		const unsigned short *pal_data = sprite->pal_data;
/*TODO*///		int x,y;
/*TODO*///		unsigned char pen;
/*TODO*///		int pitch = blit.line_offset*dy/2;
/*TODO*///		UINT16 *dest = (UINT16 *)(blit.baseaddr + blit.line_offset*y1);
/*TODO*///		int ycount = ycount0;
/*TODO*///
/*TODO*///		if( orientation & ORIENTATION_SWAP_XY ){ /* manually rotate the sprite graphics */
/*TODO*///			int xcount = xcount0;
/*TODO*///			for( x=x1; x!=x2; x+=dx ){
/*TODO*///				const unsigned char *source;
/*TODO*///				UINT16 *dest1;
/*TODO*///
/*TODO*///				ycount = ycount0;
/*TODO*///				while( xcount>=sprite->total_width ){
/*TODO*///					xcount -= sprite->total_width;
/*TODO*///					pen_data+=sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				dest1 = &dest[x];
/*TODO*///				for( y=y1; y!=y2; y+=dy ){
/*TODO*///					while( ycount>=sprite->total_height ){
/*TODO*///						ycount -= sprite->total_height;
/*TODO*///						source ++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip4; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen ) *dest1 = shade_table[*dest1];
/*TODO*///					ycount+= sprite->tile_height;
/*TODO*///					dest1 += pitch;
/*TODO*///				}
/*TODO*///skip4:
/*TODO*///				xcount += sprite->tile_width;
/*TODO*///			}
/*TODO*///		}
/*TODO*///		else {
/*TODO*///			for( y=y1; y!=y2; y+=dy ){
/*TODO*///				int xcount = xcount0;
/*TODO*///				const unsigned char *source;
/*TODO*///				while( ycount>=sprite->total_height ){
/*TODO*///					ycount -= sprite->total_height;
/*TODO*///					pen_data += sprite->line_offset;
/*TODO*///				}
/*TODO*///				source = pen_data;
/*TODO*///				for( x=x1; x!=x2; x+=dx ){
/*TODO*///					while( xcount>=sprite->total_width ){
/*TODO*///						xcount -= sprite->total_width;
/*TODO*///						source++;
/*TODO*///					}
/*TODO*///					pen = *source;
/*TODO*///					if( pen==0xff ) goto skip3; /* marker for right side of sprite; needed for AltBeast, ESwat */
/*TODO*///					if( pen ) dest[x] = shade_table[dest[x]];
/*TODO*///					xcount += sprite->tile_width;
/*TODO*///				}
/*TODO*///skip3:
/*TODO*///				ycount += sprite->tile_height;
/*TODO*///				dest += pitch;
/*TODO*///			}
/*TODO*///		}
/*TODO*///	}
/*TODO*///
/*TODO*///}
/*TODO*///
/*TODO*////*********************************************************************/
/*TODO*///
    public static void sprite_init() {
        /*TODO*///	const struct rectangle *clip = &Machine->visible_area;
/*TODO*///	int left = clip->min_x;
/*TODO*///	int top = clip->min_y;
/*TODO*///	int right = clip->max_x+1;
/*TODO*///	int bottom = clip->max_y+1;
/*TODO*///
/*TODO*///	struct osd_bitmap *bitmap = Machine->scrbitmap;
/*TODO*///	screen_baseaddr = bitmap->line[0];
/*TODO*///	screen_line_offset = bitmap->line[1]-bitmap->line[0];
/*TODO*///
/*TODO*///	orientation = Machine->orientation;
/*TODO*///	screen_width = Machine->scrbitmap->width;
/*TODO*///	screen_height = Machine->scrbitmap->height;
/*TODO*///
/*TODO*///	if( orientation & ORIENTATION_SWAP_XY ){
/*TODO*///		SWAP(left,top)
/*TODO*///		SWAP(right,bottom)
/*TODO*///	}
/*TODO*///	if( orientation & ORIENTATION_FLIP_X ){
/*TODO*///		SWAP(left,right)
/*TODO*///		left = screen_width-left;
/*TODO*///		right = screen_width-right;
/*TODO*///	}
/*TODO*///	if( orientation & ORIENTATION_FLIP_Y ){
/*TODO*///		SWAP(top,bottom)
/*TODO*///		top = screen_height-top;
/*TODO*///		bottom = screen_height-bottom;
/*TODO*///	}
/*TODO*///
/*TODO*///	screen_clip_left = left;
/*TODO*///	screen_clip_right = right;
/*TODO*///	screen_clip_top = top;
/*TODO*///	screen_clip_bottom = bottom;
    }

    public static void sprite_close() {
        /*TODO*///	struct sprite_list *sprite_list = first_sprite_list;
/*TODO*///	mask_buffer_dispose();
/*TODO*///
/*TODO*///	while( sprite_list ){
/*TODO*///		struct sprite_list *next = sprite_list->next;
/*TODO*///		free( sprite_list->sprite );
/*TODO*///		free( sprite_list );
/*TODO*///		sprite_list = next;
/*TODO*///	}
/*TODO*///	first_sprite_list = NULL;
    }
    /*TODO*///
/*TODO*///struct sprite_list *sprite_list_create( int num_sprites, int flags ){
/*TODO*///	struct sprite *sprite = calloc( num_sprites, sizeof(struct sprite) );
/*TODO*///	struct sprite_list *sprite_list = calloc( 1, sizeof(struct sprite_list) );
/*TODO*///
/*TODO*///	sprite_list->num_sprites = num_sprites;
/*TODO*///	sprite_list->special_pen = -1;
/*TODO*///	sprite_list->sprite = sprite;
/*TODO*///	sprite_list->flags = flags;
/*TODO*///
/*TODO*///	/* resource tracking */
/*TODO*///	sprite_list->next = first_sprite_list;
/*TODO*///	first_sprite_list = sprite_list;
/*TODO*///
/*TODO*///	return sprite_list; /* warning: no error checking! */
/*TODO*///}
/*TODO*///
/*TODO*///static void sprite_update_helper( struct sprite_list *sprite_list ){
/*TODO*///	struct sprite *sprite_table = sprite_list->sprite;
/*TODO*///
/*TODO*///	/* initialize constants */
/*TODO*///	blit.transparent_pen = sprite_list->transparent_pen;
/*TODO*///	blit.write_to_mask = 1;
/*TODO*///	blit.clip_left = 0;
/*TODO*///	blit.clip_top = 0;
/*TODO*///
/*TODO*///	/* make a pass to adjust for screen orientation */
/*TODO*///	if( orientation & ORIENTATION_SWAP_XY ){
/*TODO*///		struct sprite *sprite = sprite_table;
/*TODO*///		const struct sprite *finish = &sprite[sprite_list->num_sprites];
/*TODO*///		while( sprite<finish ){
/*TODO*///			SWAP(sprite->x, sprite->y)
/*TODO*///			SWAP(sprite->total_height,sprite->total_width)
/*TODO*///			SWAP(sprite->tile_width,sprite->tile_height)
/*TODO*///			SWAP(sprite->x_offset,sprite->y_offset)
/*TODO*///
/*TODO*///			/* we must also swap the flipx and flipy bits (if they aren't identical) */
/*TODO*///			if( sprite->flags&SPRITE_FLIPX ){
/*TODO*///				if( !(sprite->flags&SPRITE_FLIPY) ){
/*TODO*///					sprite->flags = (sprite->flags&~SPRITE_FLIPX)|SPRITE_FLIPY;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			else {
/*TODO*///				if( sprite->flags&SPRITE_FLIPY ){
/*TODO*///					sprite->flags = (sprite->flags&~SPRITE_FLIPY)|SPRITE_FLIPX;
/*TODO*///				}
/*TODO*///			}
/*TODO*///			sprite++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	if( orientation & ORIENTATION_FLIP_X ){
/*TODO*///		struct sprite *sprite = sprite_table;
/*TODO*///		const struct sprite *finish = &sprite[sprite_list->num_sprites];
/*TODO*///#ifndef PREROTATE_GFX
/*TODO*///		int toggle_bit = SPRITE_FLIPX;
/*TODO*///#else
/*TODO*///		int toggle_bit = (sprite_list->flags & SPRITE_LIST_RAW_DATA)?SPRITE_FLIPX:0;
/*TODO*///#endif
/*TODO*///		while( sprite<finish ){
/*TODO*///			sprite->x = screen_width - (sprite->x+sprite->total_width);
/*TODO*///			sprite->flags ^= toggle_bit;
/*TODO*///
/*TODO*///			/* extra processing for packed sprites */
/*TODO*///			sprite->x_offset = sprite->tile_width - (sprite->x_offset+sprite->total_width);
/*TODO*///			sprite++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	if( orientation & ORIENTATION_FLIP_Y ){
/*TODO*///		struct sprite *sprite = sprite_table;
/*TODO*///		const struct sprite *finish = &sprite[sprite_list->num_sprites];
/*TODO*///#ifndef PREROTATE_GFX
/*TODO*///		int toggle_bit = SPRITE_FLIPY;
/*TODO*///#else
/*TODO*///		int toggle_bit = (sprite_list->flags & SPRITE_LIST_RAW_DATA)?SPRITE_FLIPY:0;
/*TODO*///#endif
/*TODO*///		while( sprite<finish ){
/*TODO*///			sprite->y = screen_height - (sprite->y+sprite->total_height);
/*TODO*///			sprite->flags ^= toggle_bit;
/*TODO*///
/*TODO*///			/* extra processing for packed sprites */
/*TODO*///			sprite->y_offset = sprite->tile_height - (sprite->y_offset+sprite->total_height);
/*TODO*///			sprite++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	{ /* visibility check */
/*TODO*///		struct sprite *sprite = sprite_table;
/*TODO*///		const struct sprite *finish = &sprite[sprite_list->num_sprites];
/*TODO*///		while( sprite<finish ){
/*TODO*///			if( (FlickeringInvisible && (sprite->flags & SPRITE_FLICKER)) ||
/*TODO*///				sprite->total_width<=0 || sprite->total_height<=0 ||
/*TODO*///				sprite->x + sprite->total_width<=0 || sprite->x>=screen_width ||
/*TODO*///				sprite->y + sprite->total_height<=0 || sprite->y>=screen_height ){
/*TODO*///				sprite->flags &= (~SPRITE_VISIBLE);
/*TODO*///			}
/*TODO*///			sprite++;
/*TODO*///		}
/*TODO*///	}
/*TODO*///	{
/*TODO*///		int j,i, dir, last;
/*TODO*///		void (*do_blit)( const struct sprite * );
/*TODO*///
/*TODO*///		switch( sprite_list->sprite_type ){
/*TODO*///			case SPRITE_TYPE_ZOOM:
/*TODO*///			do_blit = do_blit_zoom;
/*TODO*///			return;
/*TODO*///			break;
/*TODO*///
/*TODO*///			case SPRITE_TYPE_STACK:
/*TODO*///			do_blit = do_blit_stack;
/*TODO*///			break;
/*TODO*///
/*TODO*///			case SPRITE_TYPE_UNPACK:
/*TODO*///			default:
/*TODO*///			do_blit = do_blit_unpack;
/*TODO*///			break;
/*TODO*///		}
/*TODO*///
/*TODO*///		sprite_order_setup( sprite_list, &i, &last, &dir );
/*TODO*///
/*TODO*///		for(;;){ /* process each sprite */
/*TODO*///			struct sprite *sprite = &sprite_table[i];
/*TODO*///			sprite->mask_offset = -1;
/*TODO*///
/*TODO*///			if( sprite->flags & SPRITE_VISIBLE ){
/*TODO*///				int priority = sprite->priority;
/*TODO*///
/*TODO*///				if( palette_used_colors ){
/*TODO*///					UINT32 pen_usage = sprite->pen_usage;
/*TODO*///					int indx = sprite->pal_data - Machine->remapped_colortable;
/*TODO*///					while( pen_usage ){
/*TODO*///						if( pen_usage&1 ) palette_used_colors[indx] = PALETTE_COLOR_USED;
/*TODO*///						pen_usage>>=1;
/*TODO*///						indx++;
/*TODO*///					}
/*TODO*///				}
/*TODO*///
/*TODO*///				if( i!=last && priority<sprite_list->max_priority ){
/*TODO*///					blit.origin_x = sprite->x;
/*TODO*///					blit.origin_y = sprite->y;
/*TODO*///					/* clip_left and clip_right are always zero */
/*TODO*///					blit.clip_right = sprite->total_width;
/*TODO*///					blit.clip_bottom = sprite->total_height;
/*TODO*///				/*
/*TODO*///					The following loop ensures that even though we are drawing all priority 3
/*TODO*///					sprites before drawing the priority 2 sprites, and priority 2 sprites before the
/*TODO*///					priority 1 sprites, that the sprite order as a whole still dictates
/*TODO*///					sprite-to-sprite priority when sprite pixels overlap and aren't obscured by a
/*TODO*///					background.  Checks are done to avoid special handling for the cases where
/*TODO*///					masking isn't needed.
/*TODO*///
/*TODO*///					max priority sprites are always drawn first, so we don't need to do anything
/*TODO*///					special to cause them to be obscured by other sprites
/*TODO*///				*/
/*TODO*///					j = i+dir;
/*TODO*///					for(;;){
/*TODO*///						struct sprite *front = &sprite_table[j];
/*TODO*///						if( (front->flags&SPRITE_VISIBLE) && front->priority>priority ){
/*TODO*///
/*TODO*///							if( front->x < sprite->x+sprite->total_width &&
/*TODO*///								front->y < sprite->y+sprite->total_height &&
/*TODO*///								front->x+front->total_width > sprite->x &&
/*TODO*///								front->y+front->total_height > sprite->y )
/*TODO*///							{
/*TODO*///								/* uncomment the following line to see which sprites are corrected */
/*TODO*///								//sprite->pal_data = Machine->remapped_colortable+(rand()&0xff);
/*TODO*///
/*TODO*///								if( sprite->mask_offset<0 ){ /* first masking? */
/*TODO*///									sprite->mask_offset = mask_buffer_alloc( sprite->total_width * sprite->total_height );
/*TODO*///									blit.line_offset = sprite->total_width;
/*TODO*///									blit.baseaddr = &mask_buffer[sprite->mask_offset];
/*TODO*///								}
/*TODO*///								do_blit( front );
/*TODO*///							}
/*TODO*///						}
/*TODO*///						if( j==last ) break;
/*TODO*///						j += dir;
/*TODO*///					} /* next j */
/*TODO*///				} /* priority<SPRITE_MAX_PRIORITY */
/*TODO*///			} /* visible */
/*TODO*///			if( i==last ) break;
/*TODO*///			i += dir;
/*TODO*///		} /* next i */
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///void sprite_update( void ){
/*TODO*///	struct sprite_list *sprite_list = first_sprite_list;
/*TODO*///	mask_buffer_reset();
/*TODO*///	FlickeringInvisible = !FlickeringInvisible;
/*TODO*///	while( sprite_list ){
/*TODO*///		sprite_update_helper( sprite_list );
/*TODO*///		sprite_list = sprite_list->next;
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///void sprite_draw( struct sprite_list *sprite_list, int priority ){
/*TODO*///	const struct sprite *sprite_table = sprite_list->sprite;
/*TODO*///
/*TODO*///
/*TODO*///	{ /* set constants */
/*TODO*///		blit.origin_x = 0;
/*TODO*///		blit.origin_y = 0;
/*TODO*///
/*TODO*///		blit.baseaddr = screen_baseaddr;
/*TODO*///		blit.line_offset = screen_line_offset;
/*TODO*///		blit.transparent_pen = sprite_list->transparent_pen;
/*TODO*///		blit.write_to_mask = 0;
/*TODO*///
/*TODO*///		blit.clip_left = screen_clip_left;
/*TODO*///		blit.clip_top = screen_clip_top;
/*TODO*///		blit.clip_right = screen_clip_right;
/*TODO*///		blit.clip_bottom = screen_clip_bottom;
/*TODO*///	}
/*TODO*///
/*TODO*///	{
/*TODO*///		int i, dir, last;
/*TODO*///		void (*do_blit)( const struct sprite * );
/*TODO*///
/*TODO*///		switch( sprite_list->sprite_type ){
/*TODO*///			case SPRITE_TYPE_ZOOM:
/*TODO*///			if (Machine->scrbitmap->depth == 16) /* 16 bit */
/*TODO*///			{
/*TODO*///				do_blit = do_blit_zoom16;
/*TODO*/////				return;
/*TODO*///			}
/*TODO*///			else
/*TODO*///				do_blit = do_blit_zoom;
/*TODO*///			break;
/*TODO*///
/*TODO*///			case SPRITE_TYPE_STACK:
/*TODO*///			do_blit = do_blit_stack;
/*TODO*///			break;
/*TODO*///
/*TODO*///			case SPRITE_TYPE_UNPACK:
/*TODO*///			default:
/*TODO*///			do_blit = do_blit_unpack;
/*TODO*///			break;
/*TODO*///		}
/*TODO*///
/*TODO*///		sprite_order_setup( sprite_list, &i, &last, &dir );
/*TODO*///		for(;;){
/*TODO*///			const struct sprite *sprite = &sprite_table[i];
/*TODO*///			if( (sprite->flags&SPRITE_VISIBLE) && (sprite->priority==priority) ) do_blit( sprite );
/*TODO*///			if( i==last ) break;
/*TODO*///			i+=dir;
/*TODO*///		}
/*TODO*///	}
/*TODO*///}
/*TODO*///
/*TODO*///
/*TODO*///void sprite_set_shade_table(UINT16 *table)
/*TODO*///{
/*TODO*///	shade_table=table;
/*TODO*///}
/*TODO*///    
}
