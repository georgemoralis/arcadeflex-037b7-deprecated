/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.i86.instr86.*;

public class table86H {

    static InstructionPtr i86_instruction[]
            = {
                i86_add_br8, /* 0x00 */
                i86_add_wr16, /* 0x01 */
                i86_add_r8b, /* 0x02 */
                i86_add_r16w, /* 0x03 */
                i86_add_ald8, /* 0x04 */
                i86_add_axd16, /* 0x05 */
                i86_push_es, /* 0x06 */
                i86_pop_es, /* 0x07 */
                i86_or_br8, /* 0x08 */
                i86_or_wr16, /* 0x09 */
                i86_or_r8b, /* 0x0a */
                i86_or_r16w, /* 0x0b */
                i86_or_ald8, /* 0x0c */
                i86_or_axd16, /* 0x0d */
                i86_push_cs, /* 0x0e */
                i86_invalid,
                i86_adc_br8, /* 0x10 */
                i86_adc_wr16, /* 0x11 */
                i86_adc_r8b, /* 0x12 */
                i86_adc_r16w, /* 0x13 */
                i86_adc_ald8, /* 0x14 */
                i86_adc_axd16, /* 0x15 */
                i86_push_ss, /* 0x16 */
                i86_pop_ss, /* 0x17 */
                i86_sbb_br8, /* 0x18 */
                i86_sbb_wr16, /* 0x19 */
                i86_sbb_r8b, /* 0x1a */
                i86_sbb_r16w, /* 0x1b */
                i86_sbb_ald8, /* 0x1c */
                i86_sbb_axd16, /* 0x1d */
                i86_push_ds, /* 0x1e */
                i86_pop_ds, /* 0x1f */
                i86_and_br8, /* 0x20 */
                i86_and_wr16, /* 0x21 */
                i86_and_r8b, /* 0x22 */
                i86_and_r16w, /* 0x23 */
                i86_and_ald8, /* 0x24 */
                i86_and_axd16, /* 0x25 */
                i86_es, /* 0x26 */
                i86_daa, /* 0x27 */
                i86_sub_br8, /* 0x28 */
                i86_sub_wr16, /* 0x29 */
                i86_sub_r8b, /* 0x2a */
                i86_sub_r16w, /* 0x2b */
                i86_sub_ald8, /* 0x2c */
                i86_sub_axd16, /* 0x2d */
                i86_cs, /* 0x2e */
                i86_das, /* 0x2f */
                i86_xor_br8, /* 0x30 */
                i86_xor_wr16, /* 0x31 */
                i86_xor_r8b, /* 0x32 */
                i86_xor_r16w, /* 0x33 */
                i86_xor_ald8, /* 0x34 */
                i86_xor_axd16, /* 0x35 */
                i86_ss, /* 0x36 */
                i86_aaa, /* 0x37 */
                i86_cmp_br8, /* 0x38 */
                i86_cmp_wr16, /* 0x39 */
                i86_cmp_r8b, /* 0x3a */
                i86_cmp_r16w, /* 0x3b */
                i86_cmp_ald8, /* 0x3c */
                i86_cmp_axd16, /* 0x3d */
                i86_ds, /* 0x3e */
                i86_aas, /* 0x3f */
                i86_inc_ax, /* 0x40 */
                i86_inc_cx, /* 0x41 */
                i86_inc_dx, /* 0x42 */
                i86_inc_bx, /* 0x43 */
                i86_inc_sp, /* 0x44 */
                i86_inc_bp, /* 0x45 */
                i86_inc_si, /* 0x46 */
                i86_inc_di, /* 0x47 */
                i86_dec_ax, /* 0x48 */
                i86_dec_cx, /* 0x49 */
                i86_dec_dx, /* 0x4a */
                i86_dec_bx, /* 0x4b */
                i86_dec_sp, /* 0x4c */
                i86_dec_bp, /* 0x4d */
                i86_dec_si, /* 0x4e */
                i86_dec_di, /* 0x4f */
                i86_push_ax, /* 0x50 */
                i86_push_cx, /* 0x51 */
                i86_push_dx, /* 0x52 */
                i86_push_bx, /* 0x53 */
                i86_push_sp, /* 0x54 */
                i86_push_bp, /* 0x55 */
                i86_push_si, /* 0x56 */
                i86_push_di, /* 0x57 */
                i86_pop_ax, /* 0x58 */
                i86_pop_cx, /* 0x59 */
                i86_pop_dx, /* 0x5a */
                i86_pop_bx, /* 0x5b */
                i86_pop_sp, /* 0x5c */
                i86_pop_bp, /* 0x5d */
                i86_pop_si, /* 0x5e */
                i86_pop_di, /* 0x5f */
                i86_invalid, // i86_pusha,            /* 0x60 */
                i86_invalid, // i86_popa,             /* 0x61 */
                i86_invalid, // i86_bound,            /* 0x62 */
                i86_invalid,
                i86_invalid,
                i86_invalid,
                i86_invalid,
                i86_invalid,
                i86_invalid, //i_push_d16,         /* 0x68 */
                i86_invalid, //i_imul_d16,         /* 0x69 */
                i86_invalid, //i_push_d8,          /* 0x6a */
                i86_invalid, //i_imul_d8,          /* 0x6b */
                i86_invalid, //i_insb,             /* 0x6c */
                i86_invalid, //i_insw,             /* 0x6d */
                i86_invalid, //i_outsb,            /* 0x6e */
                i86_invalid, //i_outsw,            /* 0x6f */
                i86_jo, /* 0x70 */
                i86_jno, /* 0x71 */
                i86_jb, /* 0x72 */
                i86_jnb, /* 0x73 */
                i86_jz, /* 0x74 */
                i86_jnz, /* 0x75 */
                i86_jbe, /* 0x76 */
                i86_jnbe, /* 0x77 */
                i86_js, /* 0x78 */
                i86_jns, /* 0x79 */
                i86_jp, /* 0x7a */
                i86_jnp, /* 0x7b */
                i86_jl, /* 0x7c */
                i86_jnl, /* 0x7d */
                i86_jle, /* 0x7e */
                i86_jnle, /* 0x7f */
                i86_80pre, /* 0x80 */
                i86_81pre, /* 0x81 */
                i86_82pre, /* 0x82 */
                i86_83pre, /* 0x83 */
                i86_test_br8, /* 0x84 */
                i86_test_wr16, /* 0x85 */
                i86_xchg_br8, /* 0x86 */
                i86_xchg_wr16, /* 0x87 */
                i86_mov_br8, /* 0x88 */
                i86_mov_wr16, /* 0x89 */
                i86_mov_r8b, /* 0x8a */
                i86_mov_r16w, /* 0x8b */
                i86_mov_wsreg, /* 0x8c */
                i86_lea, /* 0x8d */
                i86_mov_sregw, /* 0x8e */
                i86_popw, /* 0x8f */
                i86_nop, /* 0x90 */
                i86_xchg_axcx, /* 0x91 */
                i86_xchg_axdx, /* 0x92 */
                i86_xchg_axbx, /* 0x93 */
                i86_xchg_axsp, /* 0x94 */
                i86_xchg_axbp, /* 0x95 */
                i86_xchg_axsi, /* 0x97 */
                i86_xchg_axdi, /* 0x97 */
                i86_cbw, /* 0x98 */
                i86_cwd, /* 0x99 */
                i86_call_far, /* 0x9a */
                i86_wait, /* 0x9b */
                i86_pushf, /* 0x9c */
                i86_popf, /* 0x9d */
                i86_sahf, /* 0x9e */
                i86_lahf, /* 0x9f */
                i86_mov_aldisp, /* 0xa0 */
                i86_mov_axdisp, /* 0xa1 */
                i86_mov_dispal, /* 0xa2 */
                i86_mov_dispax, /* 0xa3 */
                i86_movsb, /* 0xa4 */
                i86_movsw, /* 0xa5 */
                i86_cmpsb, /* 0xa6 */
                i86_cmpsw, /* 0xa7 */
                i86_test_ald8, /* 0xa8 */
                i86_test_axd16, /* 0xa9 */
                i86_stosb, /* 0xaa */
                i86_stosw, /* 0xab */
                i86_lodsb, /* 0xac */
                i86_lodsw, /* 0xad */
                i86_scasb, /* 0xae */
                i86_scasw, /* 0xaf */
                i86_mov_ald8, /* 0xb0 */
                i86_mov_cld8, /* 0xb1 */
                i86_mov_dld8, /* 0xb2 */
                i86_mov_bld8, /* 0xb3 */
                i86_mov_ahd8, /* 0xb4 */
                i86_mov_chd8, /* 0xb5 */
                i86_mov_dhd8, /* 0xb6 */
                i86_mov_bhd8, /* 0xb7 */
                i86_mov_axd16, /* 0xb8 */
                i86_mov_cxd16, /* 0xb9 */
                i86_mov_dxd16, /* 0xba */
                i86_mov_bxd16, /* 0xbb */
                i86_mov_spd16, /* 0xbc */
                i86_mov_bpd16, /* 0xbd */
                i86_mov_sid16, /* 0xbe */
                i86_mov_did16, /* 0xbf */
                i86_invalid, // i86_rotshft_bd8,      /* 0xc0 */
                i86_invalid, // i86_rotshft_wd8,      /* 0xc1 */
                i86_ret_d16, /* 0xc2 */
                i86_ret, /* 0xc3 */
                i86_les_dw, /* 0xc4 */
                i86_lds_dw, /* 0xc5 */
                i86_mov_bd8, /* 0xc6 */
                i86_mov_wd16, /* 0xc7 */
                i86_invalid, //i_enter,            /* 0xc8 */
                i86_invalid, //leave,            /* 0xc9 */
                i86_retf_d16, /* 0xca */
                i86_retf, /* 0xcb */
                i86_int3, /* 0xcc */
                i86_int, /* 0xcd */
                i86_into, /* 0xce */
                i86_iret, /* 0xcf */
                i86_rotshft_b, /* 0xd0 */
                i86_rotshft_w, /* 0xd1 */
                i86_rotshft_bcl, /* 0xd2 */
                i86_rotshft_wcl, /* 0xd3 */
                i86_aam, /* 0xd4 */
                i86_aad, /* 0xd5 */
                i86_invalid,
                i86_xlat, /* 0xd7 */
                i86_escape, /* 0xd8 */
                i86_escape, /* 0xd9 */
                i86_escape, /* 0xda */
                i86_escape, /* 0xdb */
                i86_escape, /* 0xdc */
                i86_escape, /* 0xdd */
                i86_escape, /* 0xde */
                i86_escape, /* 0xdf */
                i86_loopne, /* 0xe0 */
                i86_loope, /* 0xe1 */
                i86_loop, /* 0xe2 */
                i86_jcxz, /* 0xe3 */
                i86_inal, /* 0xe4 */
                i86_inax, /* 0xe5 */
                i86_outal, /* 0xe6 */
                i86_outax, /* 0xe7 */
                i86_call_d16, /* 0xe8 */
                i86_jmp_d16, /* 0xe9 */
                i86_jmp_far, /* 0xea */
                i86_jmp_d8, /* 0xeb */
                i86_inaldx, /* 0xec */
                i86_inaxdx, /* 0xed */
                i86_outdxal, /* 0xee */
                i86_outdxax, /* 0xef */
                i86_lock, /* 0xf0 */
                i86_invalid, /* 0xf1 */
                i86_repne, /* 0xf2 */
                i86_repe, /* 0xf3 */
                i86_hlt, /* 0xf4 */
                i86_cmc, /* 0xf5 */
                i86_f6pre, /* 0xf6 */
                i86_f7pre, /* 0xf7 */
                i86_clc, /* 0xf8 */
                i86_stc, /* 0xf9 */
                i86_cli, /* 0xfa */
                i86_sti, /* 0xfb */
                i86_cld, /* 0xfc */
                i86_std, /* 0xfd */
                i86_fepre, /* 0xfe */
                i86_ffpre /* 0xff */};
}
