/*
 * ported to 0.37b7 
*/
package gr.codebb.arcadeflex.WIP.v037b7.mame;

public class spriteH {
/*TODO*///#define SPRITE_FLIPX					0x01
/*TODO*///#define SPRITE_FLIPY					0x02
/*TODO*///#define SPRITE_FLICKER					0x04
/*TODO*///#define SPRITE_VISIBLE					0x08
/*TODO*///#define SPRITE_TRANSPARENCY_THROUGH		0x10
/*TODO*///#define SPRITE_SPECIAL					0x20
/*TODO*///
/*TODO*///#define SPRITE_SHADOW					0x40
/*TODO*///#define SPRITE_PARTIAL_SHADOW			0x80
/*TODO*///
/*TODO*///typedef enum {
/*TODO*///	SPRITE_TYPE_STACK = 0,
/*TODO*///	SPRITE_TYPE_UNPACK,
/*TODO*///	SPRITE_TYPE_ZOOM
/*TODO*///} SpriteType;
/*TODO*///
/*TODO*///struct sprite {
/*TODO*///	int priority, flags;
/*TODO*///
/*TODO*///	const UINT8 *pen_data;	/* points to top left corner of tile data */
/*TODO*///	int line_offset;
/*TODO*///
/*TODO*///	const UINT16 *pal_data;
/*TODO*///	UINT32 pen_usage;
/*TODO*///
/*TODO*///	int x_offset, y_offset;
/*TODO*///	int tile_width, tile_height;
/*TODO*///	int total_width, total_height;	/* in screen coordinates */
/*TODO*///	int x, y;
/*TODO*///
/*TODO*///	int shadow_pen;
/*TODO*///
/*TODO*///	/* private */ const struct sprite *next;
/*TODO*///	/* private */ long mask_offset;
/*TODO*///};
/*TODO*///
/*TODO*////* sprite list flags */
/*TODO*///#define SPRITE_LIST_BACK_TO_FRONT	0x0
/*TODO*///#define SPRITE_LIST_FRONT_TO_BACK	0x1
/*TODO*///#define SPRITE_LIST_RAW_DATA		0x2
/*TODO*///#define SPRITE_LIST_FLIPX			0x4
/*TODO*///#define SPRITE_LIST_FLIPY			0x8
/*TODO*///
/*TODO*///struct sprite_list {
/*TODO*///	SpriteType sprite_type;
/*TODO*///	int num_sprites;
/*TODO*///	int flags;
/*TODO*///	int max_priority;
/*TODO*///	int transparent_pen;
/*TODO*///	int special_pen;
/*TODO*///
/*TODO*///	struct sprite *sprite;
/*TODO*///	struct sprite_list *next; /* resource tracking */
/*TODO*///};    
}
