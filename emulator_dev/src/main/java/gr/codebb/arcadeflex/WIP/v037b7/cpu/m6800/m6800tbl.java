/*
 * ported to v0.37b7
 *
 */
package gr.codebb.arcadeflex.WIP.v037b7.cpu.m6800;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6800.m6800ops.*;

public class m6800tbl {
    static opcode[] m6800_insn = {
        illegal, nop, illegal, illegal, illegal, illegal, tap, tpa,
        inx, dex, clv, sev, clc, sec, cli, sei,
        sba, cba, illegal, illegal, illegal, illegal, tab, tba,
        illegal, daa, illegal, aba, illegal, illegal, illegal, illegal,
        bra, brn, bhi, bls, bcc, bcs, bne, beq,
        bvc, bvs, bpl, bmi, bge, blt, bgt, ble,
        tsx, ins, pula, pulb, des, txs, psha, pshb,
        illegal, rts, illegal, rti, illegal, illegal, wai, swi,
        nega, illegal, illegal, coma, lsra, illegal, rora, asra,
        asla, rola, deca, illegal, inca, tsta, illegal, clra,
        negb, illegal, illegal, comb, lsrb, illegal, rorb, asrb,
        aslb, rolb, decb, illegal, incb, tstb, illegal, clrb,
        neg_ix, illegal, illegal, com_ix, lsr_ix, illegal, ror_ix, asr_ix,
        asl_ix, rol_ix, dec_ix, illegal, inc_ix, tst_ix, jmp_ix, clr_ix,
        neg_ex, illegal, illegal, com_ex, lsr_ex, illegal, ror_ex, asr_ex,
        asl_ex, rol_ex, dec_ex, illegal, inc_ex, tst_ex, jmp_ex, clr_ex,
        suba_im, cmpa_im, sbca_im, illegal, anda_im, bita_im, lda_im, sta_im,
        eora_im, adca_im, ora_im, adda_im, cmpx_im, bsr, lds_im, sts_im,
        suba_di, cmpa_di, sbca_di, illegal, anda_di, bita_di, lda_di, sta_di,
        eora_di, adca_di, ora_di, adda_di, cmpx_di, jsr_di, lds_di, sts_di,
        suba_ix, cmpa_ix, sbca_ix, illegal, anda_ix, bita_ix, lda_ix, sta_ix,
        eora_ix, adca_ix, ora_ix, adda_ix, cmpx_ix, jsr_ix, lds_ix, sts_ix,
        suba_ex, cmpa_ex, sbca_ex, illegal, anda_ex, bita_ex, lda_ex, sta_ex,
        eora_ex, adca_ex, ora_ex, adda_ex, cmpx_ex, jsr_ex, lds_ex, sts_ex,
        subb_im, cmpb_im, sbcb_im, illegal, andb_im, bitb_im, ldb_im, stb_im,
        eorb_im, adcb_im, orb_im, addb_im, illegal, illegal, ldx_im, stx_im,
        subb_di, cmpb_di, sbcb_di, illegal, andb_di, bitb_di, ldb_di, stb_di,
        eorb_di, adcb_di, orb_di, addb_di, illegal, illegal, ldx_di, stx_di,
        subb_ix, cmpb_ix, sbcb_ix, illegal, andb_ix, bitb_ix, ldb_ix, stb_ix,
        eorb_ix, adcb_ix, orb_ix, addb_ix, illegal, illegal, ldx_ix, stx_ix,
        subb_ex, cmpb_ex, sbcb_ex, illegal, andb_ex, bitb_ex, ldb_ex, stb_ex,
        eorb_ex, adcb_ex, orb_ex, addb_ex, illegal, illegal, ldx_ex, stx_ex
    };
/*TODO*///
/*TODO*///static void (*m6803_insn[0x100])(void) = {
/*TODO*///illegal,nop,	illegal,illegal,lsrd,	asld,	tap,	tpa,
/*TODO*///inx,	dex,	clv,	sev,	clc,	sec,	cli,	sei,
/*TODO*///sba,	cba,	illegal,illegal,illegal,illegal,tab,	tba,
/*TODO*///illegal,daa,	illegal,aba,	illegal,illegal,illegal,illegal,
/*TODO*///bra,	brn,	bhi,	bls,	bcc,	bcs,	bne,	beq,
/*TODO*///bvc,	bvs,	bpl,	bmi,	bge,	blt,	bgt,	ble,
/*TODO*///tsx,	ins,	pula,	pulb,	des,	txs,	psha,	pshb,
/*TODO*///pulx,	rts,	abx,	rti,	pshx,	mul,	wai,	swi,
/*TODO*///nega,	illegal,illegal,coma,	lsra,	illegal,rora,	asra,
/*TODO*///asla,	rola,	deca,	illegal,inca,	tsta,	illegal,clra,
/*TODO*///negb,	illegal,illegal,comb,	lsrb,	illegal,rorb,	asrb,
/*TODO*///aslb,	rolb,	decb,	illegal,incb,	tstb,	illegal,clrb,
/*TODO*///neg_ix, illegal,illegal,com_ix, lsr_ix, illegal,ror_ix, asr_ix,
/*TODO*///asl_ix, rol_ix, dec_ix, illegal,inc_ix, tst_ix, jmp_ix, clr_ix,
/*TODO*///neg_ex, illegal,illegal,com_ex, lsr_ex, illegal,ror_ex, asr_ex,
/*TODO*///asl_ex, rol_ex, dec_ex, illegal,inc_ex, tst_ex, jmp_ex, clr_ex,
/*TODO*///suba_im,cmpa_im,sbca_im,subd_im,anda_im,bita_im,lda_im, sta_im,
/*TODO*///eora_im,adca_im,ora_im, adda_im,cpx_im ,bsr,	lds_im, sts_im,
/*TODO*///suba_di,cmpa_di,sbca_di,subd_di,anda_di,bita_di,lda_di, sta_di,
/*TODO*///eora_di,adca_di,ora_di, adda_di,cpx_di ,jsr_di, lds_di, sts_di,
/*TODO*///suba_ix,cmpa_ix,sbca_ix,subd_ix,anda_ix,bita_ix,lda_ix, sta_ix,
/*TODO*///eora_ix,adca_ix,ora_ix, adda_ix,cpx_ix ,jsr_ix, lds_ix, sts_ix,
/*TODO*///suba_ex,cmpa_ex,sbca_ex,subd_ex,anda_ex,bita_ex,lda_ex, sta_ex,
/*TODO*///eora_ex,adca_ex,ora_ex, adda_ex,cpx_ex ,jsr_ex, lds_ex, sts_ex,
/*TODO*///subb_im,cmpb_im,sbcb_im,addd_im,andb_im,bitb_im,ldb_im, stb_im,
/*TODO*///eorb_im,adcb_im,orb_im, addb_im,ldd_im, std_im, ldx_im, stx_im,
/*TODO*///subb_di,cmpb_di,sbcb_di,addd_di,andb_di,bitb_di,ldb_di, stb_di,
/*TODO*///eorb_di,adcb_di,orb_di, addb_di,ldd_di, std_di, ldx_di, stx_di,
/*TODO*///subb_ix,cmpb_ix,sbcb_ix,addd_ix,andb_ix,bitb_ix,ldb_ix, stb_ix,
/*TODO*///eorb_ix,adcb_ix,orb_ix, addb_ix,ldd_ix, std_ix, ldx_ix, stx_ix,
/*TODO*///subb_ex,cmpb_ex,sbcb_ex,addd_ex,andb_ex,bitb_ex,ldb_ex, stb_ex,
/*TODO*///eorb_ex,adcb_ex,orb_ex, addb_ex,ldd_ex, std_ex, ldx_ex, stx_ex
/*TODO*///};
/*TODO*///
/*TODO*///#if (HAS_HD63701)
/*TODO*///static void (*hd63701_insn[0x100])(void) = {
/*TODO*///trap	,nop,	trap	,trap	,lsrd,	asld,	tap,	tpa,
/*TODO*///inx,	dex,	clv,	sev,	clc,	sec,	cli,	sei,
/*TODO*///sba,	cba,	undoc1, undoc2, trap	,trap	,tab,	tba,
/*TODO*///xgdx,	daa,	slp		,aba,	trap	,trap	,trap	,trap	,
/*TODO*///bra,	brn,	bhi,	bls,	bcc,	bcs,	bne,	beq,
/*TODO*///bvc,	bvs,	bpl,	bmi,	bge,	blt,	bgt,	ble,
/*TODO*///tsx,	ins,	pula,	pulb,	des,	txs,	psha,	pshb,
/*TODO*///pulx,	rts,	abx,	rti,	pshx,	mul,	wai,	swi,
/*TODO*///nega,	trap	,trap	,coma,	lsra,	trap	,rora,	asra,
/*TODO*///asla,	rola,	deca,	trap	,inca,	tsta,	trap	,clra,
/*TODO*///negb,	trap	,trap	,comb,	lsrb,	trap	,rorb,	asrb,
/*TODO*///aslb,	rolb,	decb,	trap	,incb,	tstb,	trap	,clrb,
/*TODO*///neg_ix, aim_ix, oim_ix, com_ix, lsr_ix, eim_ix, ror_ix, asr_ix,
/*TODO*///asl_ix, rol_ix, dec_ix, tim_ix, inc_ix, tst_ix, jmp_ix, clr_ix,
/*TODO*///neg_ex, aim_di, oim_di, com_ex, lsr_ex, eim_di, ror_ex, asr_ex,
/*TODO*///asl_ex, rol_ex, dec_ex, tim_di, inc_ex, tst_ex, jmp_ex, clr_ex,
/*TODO*///suba_im,cmpa_im,sbca_im,subd_im,anda_im,bita_im,lda_im, sta_im,
/*TODO*///eora_im,adca_im,ora_im, adda_im,cpx_im ,bsr,	lds_im, sts_im,
/*TODO*///suba_di,cmpa_di,sbca_di,subd_di,anda_di,bita_di,lda_di, sta_di,
/*TODO*///eora_di,adca_di,ora_di, adda_di,cpx_di ,jsr_di, lds_di, sts_di,
/*TODO*///suba_ix,cmpa_ix,sbca_ix,subd_ix,anda_ix,bita_ix,lda_ix, sta_ix,
/*TODO*///eora_ix,adca_ix,ora_ix, adda_ix,cpx_ix ,jsr_ix, lds_ix, sts_ix,
/*TODO*///suba_ex,cmpa_ex,sbca_ex,subd_ex,anda_ex,bita_ex,lda_ex, sta_ex,
/*TODO*///eora_ex,adca_ex,ora_ex, adda_ex,cpx_ex ,jsr_ex, lds_ex, sts_ex,
/*TODO*///subb_im,cmpb_im,sbcb_im,addd_im,andb_im,bitb_im,ldb_im, stb_im,
/*TODO*///eorb_im,adcb_im,orb_im, addb_im,ldd_im, std_im, ldx_im, stx_im,
/*TODO*///subb_di,cmpb_di,sbcb_di,addd_di,andb_di,bitb_di,ldb_di, stb_di,
/*TODO*///eorb_di,adcb_di,orb_di, addb_di,ldd_di, std_di, ldx_di, stx_di,
/*TODO*///subb_ix,cmpb_ix,sbcb_ix,addd_ix,andb_ix,bitb_ix,ldb_ix, stb_ix,
/*TODO*///eorb_ix,adcb_ix,orb_ix, addb_ix,ldd_ix, std_ix, ldx_ix, stx_ix,
/*TODO*///subb_ex,cmpb_ex,sbcb_ex,addd_ex,andb_ex,bitb_ex,ldb_ex, stb_ex,
/*TODO*///eorb_ex,adcb_ex,orb_ex, addb_ex,ldd_ex, std_ex, ldx_ex, stx_ex
/*TODO*///};
/*TODO*///#endif
/*TODO*///
/*TODO*///#if (HAS_NSC8105)
/*TODO*///static void (*nsc8105_insn[0x100])(void) = {
/*TODO*///illegal,illegal,nop,	illegal,illegal,tap,	illegal,tpa,
/*TODO*///inx,	clv,	dex,	sev,	clc,	cli,	sec,	sei,
/*TODO*///sba,	illegal,cba,	illegal,illegal,tab,	illegal,tba,
/*TODO*///illegal,illegal,daa,	aba,	illegal,illegal,illegal,illegal,
/*TODO*///bra,	bhi,	brn,	bls,	bcc,	bne,	bcs,	beq,
/*TODO*///bvc,	bpl,	bvs,	bmi,	bge,	bgt,	blt,	ble,
/*TODO*///tsx,	pula,	ins,	pulb,	des,	psha,	txs,	pshb,
/*TODO*///illegal,illegal,rts,	rti,	illegal,wai,	illegal,swi,
/*TODO*///suba_im,sbca_im,cmpa_im,illegal,anda_im,lda_im, bita_im,sta_im,
/*TODO*///eora_im,ora_im, adca_im,adda_im,cmpx_im,lds_im, bsr,	sts_im,
/*TODO*///suba_di,sbca_di,cmpa_di,illegal,anda_di,lda_di, bita_di,sta_di,
/*TODO*///eora_di,ora_di, adca_di,adda_di,cmpx_di,lds_di, jsr_di, sts_di,
/*TODO*///suba_ix,sbca_ix,cmpa_ix,illegal,anda_ix,lda_ix, bita_ix,sta_ix,
/*TODO*///eora_ix,ora_ix, adca_ix,adda_ix,cmpx_ix,lds_ix, jsr_ix, sts_ix,
/*TODO*///suba_ex,sbca_ex,cmpa_ex,illegal,anda_ex,lda_ex, bita_ex,sta_ex,
/*TODO*///eora_ex,ora_ex, adca_ex,adda_ex,cmpx_ex,lds_ex, jsr_ex, sts_ex,
/*TODO*///nega,	illegal,illegal,coma,	lsra,	rora,	illegal,asra,
/*TODO*///asla,	deca,	rola,	illegal,inca,	illegal,tsta,	clra,
/*TODO*///negb,	illegal,illegal,comb,	lsrb,	rorb,	illegal,asrb,
/*TODO*///aslb,	decb,	rolb,	illegal,incb,	illegal,tstb,	clrb,
/*TODO*///neg_ix, illegal,illegal,com_ix, lsr_ix, ror_ix,	illegal,asr_ix,
/*TODO*///asl_ix, dec_ix, rol_ix, illegal,inc_ix, jmp_ix, tst_ix, clr_ix,
/*TODO*///neg_ex, illegal,illegal,com_ex, lsr_ex, ror_ex,	illegal,asr_ex,
/*TODO*///asl_ex, dec_ex, rol_ex, illegal,inc_ex, jmp_ex, tst_ex, clr_ex,
/*TODO*///subb_im,sbcb_im,cmpb_im,illegal,andb_im,ldb_im, bitb_im,stb_im,
/*TODO*///eorb_im,orb_im, adcb_im,addb_im,illegal,ldx_im, illegal,stx_im,
/*TODO*///subb_di,sbcb_di,cmpb_di,illegal,andb_di,ldb_di, bitb_di,stb_di,
/*TODO*///eorb_di,orb_di, adcb_di,addb_di,illegal,ldx_di, illegal,stx_di,
/*TODO*///subb_ix,sbcb_ix,cmpb_ix,illegal,andb_ix,ldb_ix, bitb_ix,stb_ix,
/*TODO*///eorb_ix,orb_ix, adcb_ix,addb_ix,illegal,ldx_ix, illegal,stx_ix,
/*TODO*///subb_ex,sbcb_ex,cmpb_ex,illegal,andb_ex,ldb_ex, bitb_ex,stb_ex,
/*TODO*///eorb_ex,orb_ex, adcb_ex,addb_ex,addx_ex,ldx_ex, illegal,stx_ex
/*TODO*///};   
}