/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.i86;

public class i86time {

    public static class i86_timing {

        int id;

        int exception, iret;
        /* exception, IRET */
        int int3, int_imm, into_nt, into_t;
        /* INTs */
        int override;
        /* segment overrides */
        int flag_ops, lahf, sahf;
        /* flag operations */
        int aaa, aas, aam, aad;
        /* arithmetic adjusts */
        int daa, das;
        /* decimal adjusts */
        int cbw, cwd;
        /* sign extension */
        int hlt, load_ptr, lea, nop, wait, xlat;
        /* misc */

        int jmp_short, jmp_near, jmp_far;
        /* direct JMPs */
        int jmp_r16, jmp_m16, jmp_m32;
        /* indirect JMPs */
        int call_near, call_far;
        /* direct CALLs */
        int call_r16, call_m16, call_m32;
        /* indirect CALLs */
        int ret_near, ret_far, ret_near_imm, ret_far_imm;
        /* returns */
        int jcc_nt, jcc_t, jcxz_nt, jcxz_t;
        /* conditional JMPs */
        int loop_nt, loop_t, loope_nt, loope_t;
        /* loops */

        int in_imm8, in_imm16, in_dx8, in_dx16;
        /* port reads */
        int out_imm8, out_imm16, out_dx8, out_dx16;
        /* port writes */

        int mov_rr8, mov_rm8, mov_mr8;
        /* move, 8-bit */
        int mov_ri8, mov_mi8;
        /* move, 8-bit immediate */
        int mov_rr16, mov_rm16, mov_mr16;
        /* move, 16-bit */
        int mov_ri16, mov_mi16;
        /* move, 16-bit immediate */
        int mov_am8, mov_am16, mov_ma8, mov_ma16;
        /* move, AL/AX memory */
        int mov_sr, mov_sm, mov_rs, mov_ms;
        /* move, segment registers */
        int xchg_rr8, xchg_rm8;
        /* exchange, 8-bit */
        int xchg_rr16, xchg_rm16, xchg_ar16;
        /* exchange, 16-bit */

        int push_r16, push_m16, push_seg, pushf;
        /* pushes */
        int pop_r16, pop_m16, pop_seg, popf;
        /* pops */

        int alu_rr8, alu_rm8, alu_mr8;
        /* ALU ops, 8-bit */
        int alu_ri8, alu_mi8, alu_mi8_ro;
        /* ALU ops, 8-bit immediate */
        int alu_rr16, alu_rm16, alu_mr16;
        /* ALU ops, 16-bit */
        int alu_ri16, alu_mi16, alu_mi16_ro;
        /* ALU ops, 16-bit immediate */
        int alu_r16i8, alu_m16i8, alu_m16i8_ro;
        /* ALU ops, 16-bit w/8-bit immediate */
        int mul_r8, mul_r16, mul_m8, mul_m16;
        /* MUL */
        int imul_r8, imul_r16, imul_m8, imul_m16;
        /* IMUL */
        int div_r8, div_r16, div_m8, div_m16;
        /* DIV */
        int idiv_r8, idiv_r16, idiv_m8, idiv_m16;
        /* IDIV */
        int incdec_r8, incdec_r16, incdec_m8, incdec_m16;
        /* INC/DEC */
        int negnot_r8, negnot_r16, negnot_m8, negnot_m16;
        /* NEG/NOT */

        int rot_reg_1, rot_reg_base, rot_reg_bit;
        /* reg shift/rotate */
        int rot_m8_1, rot_m8_base, rot_m8_bit;
        /* m8 shift/rotate */
        int rot_m16_1, rot_m16_base, rot_m16_bit;
        /* m16 shift/rotate */

        int cmps8, rep_cmps8_base, rep_cmps8_count;
        /* CMPS 8-bit */
        int cmps16, rep_cmps16_base, rep_cmps16_count;
        /* CMPS 16-bit */
        int scas8, rep_scas8_base, rep_scas8_count;
        /* SCAS 8-bit */
        int scas16, rep_scas16_base, rep_scas16_count;
        /* SCAS 16-bit */
        int lods8, rep_lods8_base, rep_lods8_count;
        /* LODS 8-bit */
        int lods16, rep_lods16_base, rep_lods16_count;
        /* LODS 16-bit */
        int stos8, rep_stos8_base, rep_stos8_count;
        /* STOS 8-bit */
        int stos16, rep_stos16_base, rep_stos16_count;
        /* STOS 16-bit */
        int movs8, rep_movs8_base, rep_movs8_count;
        /* MOVS 8-bit */
        int movs16, rep_movs16_base, rep_movs16_count;
        /* MOVS 16-bit */

        int check1;
        /* marker to make sure we line up */

        int ins8, rep_ins8_base, rep_ins8_count;
        /* (80186) INS 8-bit */
        int ins16, rep_ins16_base, rep_ins16_count;
        /* (80186) INS 16-bit */
        int outs8, rep_outs8_base, rep_outs8_count;
        /* (80186) OUTS 8-bit */
        int outs16, rep_outs16_base, rep_outs16_count;
        /* (80186) OUTS 16-bit */
        int push_imm, pusha, popa;
        /* (80186) PUSH immediate, PUSHA/POPA */
        int imul_rri8, imul_rmi8;
        /* (80186) IMUL immediate 8-bit */
        int imul_rri16, imul_rmi16;
        /* (80186) IMUL immediate 16-bit */
        int enter0, enter1, enter_base, enter_count, leave;
        /* (80186) ENTER/LEAVE */
        int bound;
        /* (80186) BOUND */

        int check2;

        /* marker to make sure we line up */
        public i86_timing(int id, int exception, int iret, int int3, int int_imm, int into_nt, int into_t, int override, int flag_ops, int lahf, int sahf, int aaa, int aas, int aam, int aad, int daa, int das, int cbw, int cwd, int hlt, int load_ptr, int lea, int nop, int wait, int xlat, int jmp_short, int jmp_near, int jmp_far, int jmp_r16, int jmp_m16, int jmp_m32, int call_near, int call_far, int call_r16, int call_m16, int call_m32, int ret_near, int ret_far, int ret_near_imm, int ret_far_imm, int jcc_nt, int jcc_t, int jcxz_nt, int jcxz_t, int loop_nt, int loop_t, int loope_nt, int loope_t, int in_imm8, int in_imm16, int in_dx8, int in_dx16, int out_imm8, int out_imm16, int out_dx8, int out_dx16, int mov_rr8, int mov_rm8, int mov_mr8, int mov_ri8, int mov_mi8, int mov_rr16, int mov_rm16, int mov_mr16, int mov_ri16, int mov_mi16, int mov_am8, int mov_am16, int mov_ma8, int mov_ma16, int mov_sr, int mov_sm, int mov_rs, int mov_ms, int xchg_rr8, int xchg_rm8, int xchg_rr16, int xchg_rm16, int xchg_ar16, int push_r16, int push_m16, int push_seg, int pushf, int pop_r16, int pop_m16, int pop_seg, int popf, int alu_rr8, int alu_rm8, int alu_mr8, int alu_ri8, int alu_mi8, int alu_mi8_ro, int alu_rr16, int alu_rm16, int alu_mr16, int alu_ri16, int alu_mi16, int alu_mi16_ro, int alu_r16i8, int alu_m16i8, int alu_m16i8_ro, int mul_r8, int mul_r16, int mul_m8, int mul_m16, int imul_r8, int imul_r16, int imul_m8, int imul_m16, int div_r8, int div_r16, int div_m8, int div_m16, int idiv_r8, int idiv_r16, int idiv_m8, int idiv_m16, int incdec_r8, int incdec_r16, int incdec_m8, int incdec_m16, int negnot_r8, int negnot_r16, int negnot_m8, int negnot_m16, int rot_reg_1, int rot_reg_base, int rot_reg_bit, int rot_m8_1, int rot_m8_base, int rot_m8_bit, int rot_m16_1, int rot_m16_base, int rot_m16_bit, int cmps8, int rep_cmps8_base, int rep_cmps8_count, int cmps16, int rep_cmps16_base, int rep_cmps16_count, int scas8, int rep_scas8_base, int rep_scas8_count, int scas16, int rep_scas16_base, int rep_scas16_count, int lods8, int rep_lods8_base, int rep_lods8_count, int lods16, int rep_lods16_base, int rep_lods16_count, int stos8, int rep_stos8_base, int rep_stos8_count, int stos16, int rep_stos16_base, int rep_stos16_count, int movs8, int rep_movs8_base, int rep_movs8_count, int movs16, int rep_movs16_base, int rep_movs16_count, int check1, int ins8, int rep_ins8_base, int rep_ins8_count, int ins16, int rep_ins16_base, int rep_ins16_count, int outs8, int rep_outs8_base, int rep_outs8_count, int outs16, int rep_outs16_base, int rep_outs16_count, int push_imm, int pusha, int popa, int imul_rri8, int imul_rmi8, int imul_rri16, int imul_rmi16, int enter0, int enter1, int enter_base, int enter_count, int leave, int bound, int check2) {
            this.id = id;
            this.exception = exception;
            this.iret = iret;
            this.int3 = int3;
            this.int_imm = int_imm;
            this.into_nt = into_nt;
            this.into_t = into_t;
            this.override = override;
            this.flag_ops = flag_ops;
            this.lahf = lahf;
            this.sahf = sahf;
            this.aaa = aaa;
            this.aas = aas;
            this.aam = aam;
            this.aad = aad;
            this.daa = daa;
            this.das = das;
            this.cbw = cbw;
            this.cwd = cwd;
            this.hlt = hlt;
            this.load_ptr = load_ptr;
            this.lea = lea;
            this.nop = nop;
            this.wait = wait;
            this.xlat = xlat;
            this.jmp_short = jmp_short;
            this.jmp_near = jmp_near;
            this.jmp_far = jmp_far;
            this.jmp_r16 = jmp_r16;
            this.jmp_m16 = jmp_m16;
            this.jmp_m32 = jmp_m32;
            this.call_near = call_near;
            this.call_far = call_far;
            this.call_r16 = call_r16;
            this.call_m16 = call_m16;
            this.call_m32 = call_m32;
            this.ret_near = ret_near;
            this.ret_far = ret_far;
            this.ret_near_imm = ret_near_imm;
            this.ret_far_imm = ret_far_imm;
            this.jcc_nt = jcc_nt;
            this.jcc_t = jcc_t;
            this.jcxz_nt = jcxz_nt;
            this.jcxz_t = jcxz_t;
            this.loop_nt = loop_nt;
            this.loop_t = loop_t;
            this.loope_nt = loope_nt;
            this.loope_t = loope_t;
            this.in_imm8 = in_imm8;
            this.in_imm16 = in_imm16;
            this.in_dx8 = in_dx8;
            this.in_dx16 = in_dx16;
            this.out_imm8 = out_imm8;
            this.out_imm16 = out_imm16;
            this.out_dx8 = out_dx8;
            this.out_dx16 = out_dx16;
            this.mov_rr8 = mov_rr8;
            this.mov_rm8 = mov_rm8;
            this.mov_mr8 = mov_mr8;
            this.mov_ri8 = mov_ri8;
            this.mov_mi8 = mov_mi8;
            this.mov_rr16 = mov_rr16;
            this.mov_rm16 = mov_rm16;
            this.mov_mr16 = mov_mr16;
            this.mov_ri16 = mov_ri16;
            this.mov_mi16 = mov_mi16;
            this.mov_am8 = mov_am8;
            this.mov_am16 = mov_am16;
            this.mov_ma8 = mov_ma8;
            this.mov_ma16 = mov_ma16;
            this.mov_sr = mov_sr;
            this.mov_sm = mov_sm;
            this.mov_rs = mov_rs;
            this.mov_ms = mov_ms;
            this.xchg_rr8 = xchg_rr8;
            this.xchg_rm8 = xchg_rm8;
            this.xchg_rr16 = xchg_rr16;
            this.xchg_rm16 = xchg_rm16;
            this.xchg_ar16 = xchg_ar16;
            this.push_r16 = push_r16;
            this.push_m16 = push_m16;
            this.push_seg = push_seg;
            this.pushf = pushf;
            this.pop_r16 = pop_r16;
            this.pop_m16 = pop_m16;
            this.pop_seg = pop_seg;
            this.popf = popf;
            this.alu_rr8 = alu_rr8;
            this.alu_rm8 = alu_rm8;
            this.alu_mr8 = alu_mr8;
            this.alu_ri8 = alu_ri8;
            this.alu_mi8 = alu_mi8;
            this.alu_mi8_ro = alu_mi8_ro;
            this.alu_rr16 = alu_rr16;
            this.alu_rm16 = alu_rm16;
            this.alu_mr16 = alu_mr16;
            this.alu_ri16 = alu_ri16;
            this.alu_mi16 = alu_mi16;
            this.alu_mi16_ro = alu_mi16_ro;
            this.alu_r16i8 = alu_r16i8;
            this.alu_m16i8 = alu_m16i8;
            this.alu_m16i8_ro = alu_m16i8_ro;
            this.mul_r8 = mul_r8;
            this.mul_r16 = mul_r16;
            this.mul_m8 = mul_m8;
            this.mul_m16 = mul_m16;
            this.imul_r8 = imul_r8;
            this.imul_r16 = imul_r16;
            this.imul_m8 = imul_m8;
            this.imul_m16 = imul_m16;
            this.div_r8 = div_r8;
            this.div_r16 = div_r16;
            this.div_m8 = div_m8;
            this.div_m16 = div_m16;
            this.idiv_r8 = idiv_r8;
            this.idiv_r16 = idiv_r16;
            this.idiv_m8 = idiv_m8;
            this.idiv_m16 = idiv_m16;
            this.incdec_r8 = incdec_r8;
            this.incdec_r16 = incdec_r16;
            this.incdec_m8 = incdec_m8;
            this.incdec_m16 = incdec_m16;
            this.negnot_r8 = negnot_r8;
            this.negnot_r16 = negnot_r16;
            this.negnot_m8 = negnot_m8;
            this.negnot_m16 = negnot_m16;
            this.rot_reg_1 = rot_reg_1;
            this.rot_reg_base = rot_reg_base;
            this.rot_reg_bit = rot_reg_bit;
            this.rot_m8_1 = rot_m8_1;
            this.rot_m8_base = rot_m8_base;
            this.rot_m8_bit = rot_m8_bit;
            this.rot_m16_1 = rot_m16_1;
            this.rot_m16_base = rot_m16_base;
            this.rot_m16_bit = rot_m16_bit;
            this.cmps8 = cmps8;
            this.rep_cmps8_base = rep_cmps8_base;
            this.rep_cmps8_count = rep_cmps8_count;
            this.cmps16 = cmps16;
            this.rep_cmps16_base = rep_cmps16_base;
            this.rep_cmps16_count = rep_cmps16_count;
            this.scas8 = scas8;
            this.rep_scas8_base = rep_scas8_base;
            this.rep_scas8_count = rep_scas8_count;
            this.scas16 = scas16;
            this.rep_scas16_base = rep_scas16_base;
            this.rep_scas16_count = rep_scas16_count;
            this.lods8 = lods8;
            this.rep_lods8_base = rep_lods8_base;
            this.rep_lods8_count = rep_lods8_count;
            this.lods16 = lods16;
            this.rep_lods16_base = rep_lods16_base;
            this.rep_lods16_count = rep_lods16_count;
            this.stos8 = stos8;
            this.rep_stos8_base = rep_stos8_base;
            this.rep_stos8_count = rep_stos8_count;
            this.stos16 = stos16;
            this.rep_stos16_base = rep_stos16_base;
            this.rep_stos16_count = rep_stos16_count;
            this.movs8 = movs8;
            this.rep_movs8_base = rep_movs8_base;
            this.rep_movs8_count = rep_movs8_count;
            this.movs16 = movs16;
            this.rep_movs16_base = rep_movs16_base;
            this.rep_movs16_count = rep_movs16_count;
            this.check1 = check1;
            this.ins8 = ins8;
            this.rep_ins8_base = rep_ins8_base;
            this.rep_ins8_count = rep_ins8_count;
            this.ins16 = ins16;
            this.rep_ins16_base = rep_ins16_base;
            this.rep_ins16_count = rep_ins16_count;
            this.outs8 = outs8;
            this.rep_outs8_base = rep_outs8_base;
            this.rep_outs8_count = rep_outs8_count;
            this.outs16 = outs16;
            this.rep_outs16_base = rep_outs16_base;
            this.rep_outs16_count = rep_outs16_count;
            this.push_imm = push_imm;
            this.pusha = pusha;
            this.popa = popa;
            this.imul_rri8 = imul_rri8;
            this.imul_rmi8 = imul_rmi8;
            this.imul_rri16 = imul_rri16;
            this.imul_rmi16 = imul_rmi16;
            this.enter0 = enter0;
            this.enter1 = enter1;
            this.enter_base = enter_base;
            this.enter_count = enter_count;
            this.leave = leave;
            this.bound = bound;
            this.check2 = check2;
        }

        public i86_timing(int id, int exception, int iret, int int3, int int_imm, int into_nt, int into_t, int override, int flag_ops, int lahf, int sahf, int aaa, int aas, int aam, int aad, int daa, int das, int cbw, int cwd, int hlt, int load_ptr, int lea, int nop, int wait, int xlat, int jmp_short, int jmp_near, int jmp_far, int jmp_r16, int jmp_m16, int jmp_m32, int call_near, int call_far, int call_r16, int call_m16, int call_m32, int ret_near, int ret_far, int ret_near_imm, int ret_far_imm, int jcc_nt, int jcc_t, int jcxz_nt, int jcxz_t, int loop_nt, int loop_t, int loope_nt, int loope_t, int in_imm8, int in_imm16, int in_dx8, int in_dx16, int out_imm8, int out_imm16, int out_dx8, int out_dx16, int mov_rr8, int mov_rm8, int mov_mr8, int mov_ri8, int mov_mi8, int mov_rr16, int mov_rm16, int mov_mr16, int mov_ri16, int mov_mi16, int mov_am8, int mov_am16, int mov_ma8, int mov_ma16, int mov_sr, int mov_sm, int mov_rs, int mov_ms, int xchg_rr8, int xchg_rm8, int xchg_rr16, int xchg_rm16, int xchg_ar16, int push_r16, int push_m16, int push_seg, int pushf, int pop_r16, int pop_m16, int pop_seg, int popf, int alu_rr8, int alu_rm8, int alu_mr8, int alu_ri8, int alu_mi8, int alu_mi8_ro, int alu_rr16, int alu_rm16, int alu_mr16, int alu_ri16, int alu_mi16, int alu_mi16_ro, int alu_r16i8, int alu_m16i8, int alu_m16i8_ro, int mul_r8, int mul_r16, int mul_m8, int mul_m16, int imul_r8, int imul_r16, int imul_m8, int imul_m16, int div_r8, int div_r16, int div_m8, int div_m16, int idiv_r8, int idiv_r16, int idiv_m8, int idiv_m16, int incdec_r8, int incdec_r16, int incdec_m8, int incdec_m16, int negnot_r8, int negnot_r16, int negnot_m8, int negnot_m16, int rot_reg_1, int rot_reg_base, int rot_reg_bit, int rot_m8_1, int rot_m8_base, int rot_m8_bit, int rot_m16_1, int rot_m16_base, int rot_m16_bit, int cmps8, int rep_cmps8_base, int rep_cmps8_count, int cmps16, int rep_cmps16_base, int rep_cmps16_count, int scas8, int rep_scas8_base, int rep_scas8_count, int scas16, int rep_scas16_base, int rep_scas16_count, int lods8, int rep_lods8_base, int rep_lods8_count, int lods16, int rep_lods16_base, int rep_lods16_count, int stos8, int rep_stos8_base, int rep_stos8_count, int stos16, int rep_stos16_base, int rep_stos16_count, int movs8, int rep_movs8_base, int rep_movs8_count, int movs16, int rep_movs16_base, int rep_movs16_count, int check1) {
            this.id = id;
            this.exception = exception;
            this.iret = iret;
            this.int3 = int3;
            this.int_imm = int_imm;
            this.into_nt = into_nt;
            this.into_t = into_t;
            this.override = override;
            this.flag_ops = flag_ops;
            this.lahf = lahf;
            this.sahf = sahf;
            this.aaa = aaa;
            this.aas = aas;
            this.aam = aam;
            this.aad = aad;
            this.daa = daa;
            this.das = das;
            this.cbw = cbw;
            this.cwd = cwd;
            this.hlt = hlt;
            this.load_ptr = load_ptr;
            this.lea = lea;
            this.nop = nop;
            this.wait = wait;
            this.xlat = xlat;
            this.jmp_short = jmp_short;
            this.jmp_near = jmp_near;
            this.jmp_far = jmp_far;
            this.jmp_r16 = jmp_r16;
            this.jmp_m16 = jmp_m16;
            this.jmp_m32 = jmp_m32;
            this.call_near = call_near;
            this.call_far = call_far;
            this.call_r16 = call_r16;
            this.call_m16 = call_m16;
            this.call_m32 = call_m32;
            this.ret_near = ret_near;
            this.ret_far = ret_far;
            this.ret_near_imm = ret_near_imm;
            this.ret_far_imm = ret_far_imm;
            this.jcc_nt = jcc_nt;
            this.jcc_t = jcc_t;
            this.jcxz_nt = jcxz_nt;
            this.jcxz_t = jcxz_t;
            this.loop_nt = loop_nt;
            this.loop_t = loop_t;
            this.loope_nt = loope_nt;
            this.loope_t = loope_t;
            this.in_imm8 = in_imm8;
            this.in_imm16 = in_imm16;
            this.in_dx8 = in_dx8;
            this.in_dx16 = in_dx16;
            this.out_imm8 = out_imm8;
            this.out_imm16 = out_imm16;
            this.out_dx8 = out_dx8;
            this.out_dx16 = out_dx16;
            this.mov_rr8 = mov_rr8;
            this.mov_rm8 = mov_rm8;
            this.mov_mr8 = mov_mr8;
            this.mov_ri8 = mov_ri8;
            this.mov_mi8 = mov_mi8;
            this.mov_rr16 = mov_rr16;
            this.mov_rm16 = mov_rm16;
            this.mov_mr16 = mov_mr16;
            this.mov_ri16 = mov_ri16;
            this.mov_mi16 = mov_mi16;
            this.mov_am8 = mov_am8;
            this.mov_am16 = mov_am16;
            this.mov_ma8 = mov_ma8;
            this.mov_ma16 = mov_ma16;
            this.mov_sr = mov_sr;
            this.mov_sm = mov_sm;
            this.mov_rs = mov_rs;
            this.mov_ms = mov_ms;
            this.xchg_rr8 = xchg_rr8;
            this.xchg_rm8 = xchg_rm8;
            this.xchg_rr16 = xchg_rr16;
            this.xchg_rm16 = xchg_rm16;
            this.xchg_ar16 = xchg_ar16;
            this.push_r16 = push_r16;
            this.push_m16 = push_m16;
            this.push_seg = push_seg;
            this.pushf = pushf;
            this.pop_r16 = pop_r16;
            this.pop_m16 = pop_m16;
            this.pop_seg = pop_seg;
            this.popf = popf;
            this.alu_rr8 = alu_rr8;
            this.alu_rm8 = alu_rm8;
            this.alu_mr8 = alu_mr8;
            this.alu_ri8 = alu_ri8;
            this.alu_mi8 = alu_mi8;
            this.alu_mi8_ro = alu_mi8_ro;
            this.alu_rr16 = alu_rr16;
            this.alu_rm16 = alu_rm16;
            this.alu_mr16 = alu_mr16;
            this.alu_ri16 = alu_ri16;
            this.alu_mi16 = alu_mi16;
            this.alu_mi16_ro = alu_mi16_ro;
            this.alu_r16i8 = alu_r16i8;
            this.alu_m16i8 = alu_m16i8;
            this.alu_m16i8_ro = alu_m16i8_ro;
            this.mul_r8 = mul_r8;
            this.mul_r16 = mul_r16;
            this.mul_m8 = mul_m8;
            this.mul_m16 = mul_m16;
            this.imul_r8 = imul_r8;
            this.imul_r16 = imul_r16;
            this.imul_m8 = imul_m8;
            this.imul_m16 = imul_m16;
            this.div_r8 = div_r8;
            this.div_r16 = div_r16;
            this.div_m8 = div_m8;
            this.div_m16 = div_m16;
            this.idiv_r8 = idiv_r8;
            this.idiv_r16 = idiv_r16;
            this.idiv_m8 = idiv_m8;
            this.idiv_m16 = idiv_m16;
            this.incdec_r8 = incdec_r8;
            this.incdec_r16 = incdec_r16;
            this.incdec_m8 = incdec_m8;
            this.incdec_m16 = incdec_m16;
            this.negnot_r8 = negnot_r8;
            this.negnot_r16 = negnot_r16;
            this.negnot_m8 = negnot_m8;
            this.negnot_m16 = negnot_m16;
            this.rot_reg_1 = rot_reg_1;
            this.rot_reg_base = rot_reg_base;
            this.rot_reg_bit = rot_reg_bit;
            this.rot_m8_1 = rot_m8_1;
            this.rot_m8_base = rot_m8_base;
            this.rot_m8_bit = rot_m8_bit;
            this.rot_m16_1 = rot_m16_1;
            this.rot_m16_base = rot_m16_base;
            this.rot_m16_bit = rot_m16_bit;
            this.cmps8 = cmps8;
            this.rep_cmps8_base = rep_cmps8_base;
            this.rep_cmps8_count = rep_cmps8_count;
            this.cmps16 = cmps16;
            this.rep_cmps16_base = rep_cmps16_base;
            this.rep_cmps16_count = rep_cmps16_count;
            this.scas8 = scas8;
            this.rep_scas8_base = rep_scas8_base;
            this.rep_scas8_count = rep_scas8_count;
            this.scas16 = scas16;
            this.rep_scas16_base = rep_scas16_base;
            this.rep_scas16_count = rep_scas16_count;
            this.lods8 = lods8;
            this.rep_lods8_base = rep_lods8_base;
            this.rep_lods8_count = rep_lods8_count;
            this.lods16 = lods16;
            this.rep_lods16_base = rep_lods16_base;
            this.rep_lods16_count = rep_lods16_count;
            this.stos8 = stos8;
            this.rep_stos8_base = rep_stos8_base;
            this.rep_stos8_count = rep_stos8_count;
            this.stos16 = stos16;
            this.rep_stos16_base = rep_stos16_base;
            this.rep_stos16_count = rep_stos16_count;
            this.movs8 = movs8;
            this.rep_movs8_base = rep_movs8_base;
            this.rep_movs8_count = rep_movs8_count;
            this.movs16 = movs16;
            this.rep_movs16_base = rep_movs16_base;
            this.rep_movs16_count = rep_movs16_count;
            this.check1 = check1;
        }
        
    }


    /* these come from the 8088 timings in OPCODE.LST, but with the
   penalty for 16-bit memory accesses removed wherever possible */
    static i86_timing i86_cycles = new i86_timing(
            8086,
            51, 32, /* exception, IRET */
            2, 0, 4, 2, /* INTs */
            2, /* segment overrides */
            2, 4, 4, /* flag operations */
            4, 4, 83, 60, /* arithmetic adjusts */
            4, 4, /* decimal adjusts */
            2, 5, /* sign extension */
            2, 24, 2, 2, 3, 11, /* misc */
            15, 15, 15, /* direct JMPs */
            11, 18, 24, /* indirect JMPs */
            19, 28, /* direct CALLs */
            16, 21, 37, /* indirect CALLs */
            20, 32, 24, 31, /* returns */
            4, 16, 6, 18, /* conditional JMPs */
            5, 17, 6, 18, /* loops */
            10, 14, 8, 12, /* port reads */
            10, 14, 8, 12, /* port writes */
            2, 8, 9, /* move, 8-bit */
            4, 10, /* move, 8-bit immediate */
            2, 8, 9, /* move, 16-bit */
            4, 10, /* move, 16-bit immediate */
            10, 10, 10, 10, /* move, AL/AX memory */
            2, 8, 2, 9, /* move, segment registers */
            4, 17, /* exchange, 8-bit */
            4, 17, 3, /* exchange, 16-bit */
            15, 24, 14, 14, /* pushes */
            12, 25, 12, 12, /* pops */
            3, 9, 16, /* ALU ops, 8-bit */
            4, 17, 10, /* ALU ops, 8-bit immediate */
            3, 9, 16, /* ALU ops, 16-bit */
            4, 17, 10, /* ALU ops, 16-bit immediate */
            4, 17, 10, /* ALU ops, 16-bit w/8-bit immediate */
            70, 118, 76, 128, /* MUL */
            80, 128, 86, 138, /* IMUL */
            80, 144, 86, 154, /* DIV */
            101, 165, 107, 175,/* IDIV */
            3, 2, 15, 15, /* INC/DEC */
            3, 3, 16, 16, /* NEG/NOT */
            2, 8, 4, /* reg shift/rotate */
            15, 20, 4, /* m8 shift/rotate */
            15, 20, 4, /* m16 shift/rotate */
            22, 9, 21, /* CMPS 8-bit */
            22, 9, 21, /* CMPS 16-bit */
            15, 9, 14, /* SCAS 8-bit */
            15, 9, 14, /* SCAS 16-bit */
            12, 9, 11, /* LODS 8-bit */
            12, 9, 11, /* LODS 16-bit */
            11, 9, 10, /* STOS 8-bit */
            11, 9, 10, /* STOS 16-bit */
            18, 9, 17, /* MOVS 8-bit */
            18, 9, 17, /* MOVS 16-bit */
            -1 /* marker to make sure we line up */
    );

    /* these come from the Intel 80186 datasheet */
    static i86_timing i186_cycles = new i86_timing(
            80186,
            45, 28, /* exception, IRET */
            0, 2, 4, 3, /* INTs */
            2, /* segment overrides */
            2, 2, 3, /* flag operations */
            8, 7, 19, 15, /* arithmetic adjusts */
            4, 4, /* decimal adjusts */
            2, 4, /* sign extension */
            2, 18, 6, 2, 6, 11, /* misc */
            14, 14, 14, /* direct JMPs */
            11, 17, 26, /* indirect JMPs */
            15, 23, /* direct CALLs */
            13, 19, 38, /* indirect CALLs */
            16, 22, 18, 25, /* returns */
            4, 13, 5, 15, /* conditional JMPs */
            6, 16, 6, 16, /* loops */
            10, 10, 8, 8, /* port reads */
            9, 9, 7, 7, /* port writes */
            2, 9, 12, /* move, 8-bit */
            3, 12, /* move, 8-bit immediate */
            2, 9, 12, /* move, 16-bit */
            4, 13, /* move, 16-bit immediate */
            8, 8, 9, 9, /* move, AL/AX memory */
            2, 11, 2, 11, /* move, segment registers */
            4, 17, /* exchange, 8-bit */
            4, 17, 3, /* exchange, 16-bit */
            10, 16, 9, 9, /* pushes */
            10, 20, 8, 8, /* pops */
            3, 10, 10, /* ALU ops, 8-bit */
            4, 16, 10, /* ALU ops, 8-bit immediate */
            3, 10, 10, /* ALU ops, 16-bit */
            4, 16, 10, /* ALU ops, 16-bit immediate */
            4, 16, 10, /* ALU ops, 16-bit w/8-bit immediate */
            26, 35, 32, 41, /* MUL */
            25, 34, 31, 40, /* IMUL */
            29, 38, 35, 44, /* DIV */
            44, 53, 50, 59, /* IDIV */
            3, 3, 15, 15, /* INC/DEC */
            3, 3, 10, 10, /* NEG/NOT */
            2, 5, 1, /* reg shift/rotate */
            15, 17, 1, /* m8 shift/rotate */
            15, 17, 1, /* m16 shift/rotate */
            22, 5, 22, /* CMPS 8-bit */
            22, 5, 22, /* CMPS 16-bit */
            15, 5, 15, /* SCAS 8-bit */
            15, 5, 15, /* SCAS 16-bit */
            12, 6, 11, /* LODS 8-bit */
            12, 6, 11, /* LODS 16-bit */
            10, 6, 9, /* STOS 8-bit */
            10, 6, 9, /* STOS 16-bit */
            14, 8, 8, /* MOVS 8-bit */
            14, 8, 8, /* MOVS 16-bit */
            -1, /* marker to make sure we line up */
            14, 8, 8, /* (80186) INS 8-bit */
            14, 8, 8, /* (80186) INS 16-bit */
            14, 8, 8, /* (80186) OUTS 8-bit */
            14, 8, 8, /* (80186) OUTS 16-bit */
            14, 68, 83, /* (80186) PUSH immediate, PUSHA/POPA */
            22, 29, /* (80186) IMUL immediate 8-bit */
            25, 32, /* (80186) IMUL immediate 16-bit */
            15, 25, 4, 16, 8, /* (80186) ENTER/LEAVE */
            33, /* (80186) BOUND */
            -1 /* marker to make sure we line up */
    );
    /*TODO*///
/*TODO*///
/*TODO*////* these come from the 80286 timings in OPCODE.LST */
/*TODO*////* many of these numbers are suspect */
/*TODO*///static const struct i86_timing i286_cycles =
/*TODO*///{
/*TODO*///	80286,
/*TODO*///
/*TODO*///	23,17,			/* exception, IRET */
/*TODO*///	 0, 2, 3, 1,	/* INTs */
/*TODO*///	 2,				/* segment overrides */
/*TODO*///	 2, 2, 2,		/* flag operations */
/*TODO*///	 3, 3,16,14,	/* arithmetic adjusts */
/*TODO*///	 3, 3,			/* decimal adjusts */
/*TODO*///	 2, 2,			/* sign extension */
/*TODO*///	 2, 7, 3, 3, 3, 5,	/* misc */
/*TODO*///	 
/*TODO*///	 7, 7,11,		/* direct JMPs */
/*TODO*///	 7,11,26,		/* indirect JMPs */
/*TODO*///	 7,13,			/* direct CALLs */
/*TODO*///	 7,11,29,		/* indirect CALLs */
/*TODO*///	11,15,11,15,	/* returns */
/*TODO*///	 3, 7, 4, 8,	/* conditional JMPs */
/*TODO*///	 4, 8, 4, 8,	/* loops */
/*TODO*///	
/*TODO*///	 5, 5, 5, 5,	/* port reads */
/*TODO*///	 3, 3, 3, 3,	/* port writes */
/*TODO*///
/*TODO*///	 2, 3, 3,		/* move, 8-bit */
/*TODO*///	 2, 3,			/* move, 8-bit immediate */
/*TODO*///	 2, 3, 3,		/* move, 16-bit */
/*TODO*///	 2, 3,			/* move, 16-bit immediate */
/*TODO*///	 5, 5, 3, 3,	/* move, AL/AX memory */
/*TODO*///	 2, 5, 2, 3,	/* move, segment registers */
/*TODO*///	 3, 5,			/* exchange, 8-bit */
/*TODO*///	 3, 5, 3,		/* exchange, 16-bit */
/*TODO*///	 
/*TODO*///	 5, 5, 3, 3,	/* pushes */
/*TODO*///	 5, 5, 5, 5,	/* pops */
/*TODO*///
/*TODO*///	 2, 7, 7,		/* ALU ops, 8-bit */
/*TODO*///	 3, 7, 7,		/* ALU ops, 8-bit immediate */
/*TODO*///	 2, 7, 7,		/* ALU ops, 16-bit */
/*TODO*///	 3, 7, 7,		/* ALU ops, 16-bit immediate */
/*TODO*///	 3, 7, 7,		/* ALU ops, 16-bit w/8-bit immediate */
/*TODO*///	13,21,16,24,	/* MUL */
/*TODO*///	13,21,16,24,	/* IMUL */
/*TODO*///	14,22,17,25,	/* DIV */
/*TODO*///	17,25,20,28,	/* IDIV */
/*TODO*///	 2, 2, 7, 7,	/* INC/DEC */
/*TODO*///	 2, 2, 7, 7,	/* NEG/NOT */
/*TODO*///	 
/*TODO*///	 2, 5, 0,		/* reg shift/rotate */
/*TODO*///	 7, 8, 1,		/* m8 shift/rotate */
/*TODO*///	 7, 8, 1,		/* m16 shift/rotate */
/*TODO*///	
/*TODO*///	13, 5,12,		/* CMPS 8-bit */
/*TODO*///	13, 5,12,		/* CMPS 16-bit */
/*TODO*///	 9, 5, 8,		/* SCAS 8-bit */
/*TODO*///	 9, 5, 8,		/* SCAS 16-bit */
/*TODO*///	 5, 5, 4,		/* LODS 8-bit */
/*TODO*///	 5, 5, 4,		/* LODS 16-bit */
/*TODO*///	 4, 4, 3,		/* STOS 8-bit */
/*TODO*///	 4, 4, 3,		/* STOS 16-bit */
/*TODO*///	 5, 5, 4,		/* MOVS 8-bit */
/*TODO*///	 5, 5, 4,		/* MOVS 16-bit */
/*TODO*///	
/*TODO*///	(void *)-1,		/* marker to make sure we line up */
/*TODO*///
/*TODO*///	 5, 5, 4,		/* (80186) INS 8-bit */
/*TODO*///	 5, 5, 4,		/* (80186) INS 16-bit */
/*TODO*///	 5, 5, 4,		/* (80186) OUTS 8-bit */
/*TODO*///	 5, 5, 4,		/* (80186) OUTS 16-bit */
/*TODO*///	 3,17,19,		/* (80186) PUSH immediate, PUSHA/POPA */
/*TODO*///	21,24,			/* (80186) IMUL immediate 8-bit */
/*TODO*///	21,24,			/* (80186) IMUL immediate 16-bit */
/*TODO*///	11,15,12, 4, 5,	/* (80186) ENTER/LEAVE */
/*TODO*///	13,				/* (80186) BOUND */
/*TODO*///
/*TODO*///	(void *)-1		/* marker to make sure we line up */
/*TODO*///};
/*TODO*/// 
}
