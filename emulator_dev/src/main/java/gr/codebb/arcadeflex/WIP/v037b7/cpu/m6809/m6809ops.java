
package gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809;

import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809.m6809.M6809_LDS;
import static gr.codebb.arcadeflex.WIP.v037b7.cpu.m6809.m6809.M6809_SYNC;
import static gr.codebb.arcadeflex.WIP.v037b7.mame.cpuintrf.*;
import static gr.codebb.arcadeflex.old.arcadeflex.libc_old.fprintf;


public class m6809ops {
    
    private m6809 _cpu=null;
    
    public m6809ops(m6809 _cpu){
        this._cpu = _cpu;
    }
    
    int getDreg()//compose dreg
    {
         return (_cpu._m6809.a << 8 | _cpu._m6809.b)&0xFFFF;
    }
    
    void setDreg(int reg) //write to dreg
    { 
        _cpu._m6809.a = (char)(reg >>> 8 & 0xFF);
        _cpu._m6809.b = (char)(reg & 0xFF);
    }

    public void illegal()
    {
        if( _cpu.m6809log!=null )
    		fprintf(_cpu.m6809log, "M6809: illegal opcode at %04x\n",_cpu._m6809.pc);
    }
    /* $00 NEG direct ?**** */
    public void neg_di()
    {
    	int r,t;
    	t=_cpu.DIRBYTE();
    	r = -t & 0xFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(0,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d neg_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $03 COM direct -**01 */
    public void com_di()
    {
        int t=	_cpu.DIRBYTE();
    	t = ~t & 0xFF;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(t);
    	_cpu.SEC();
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d com_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $04 LSR direct -0*-* */
    public void lsr_di()
    {
        int t=_cpu.DIRBYTE();
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (t & _cpu.CC_C);
    	t =t>>> 1 & 0xFF;
   	_cpu.SET_Z8(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lsr_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $06 ROR direct -**-* */
    public void ror_di()//suspicious recheck
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r= ((_cpu._m6809.cc & _cpu.CC_C) << 7) & 0xFF;
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (t & _cpu.CC_C);
    	r = (r | t>>>1)&0xFF;
    	_cpu.SET_NZ8(r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ror_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $07 ASR direct ?**-* */
    public void asr_di()
    {
        int t=_cpu.DIRBYTE();
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (t & _cpu.CC_C);
    	t = ((t & 0x80) | (t >>> 1))&0xFF;
    	_cpu.SET_NZ8(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asr_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $08 ASL direct ?**** */
    public void asl_di()
    {
        int t,r;
    	t=_cpu.DIRBYTE();
    	r = t << 1 & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(t,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asl_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $09 ROL direct -**** */
    public void rol_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = (_cpu._m6809.cc & _cpu.CC_C) | (t << 1);
    	_cpu.CLR_NZVC();
   	_cpu.SET_FLAGS8(t,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rol_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $0A DEC direct -***- */
    public void dec_di()
    {
        int t=_cpu.DIRBYTE();
	t= t-1 & 0xFF;
    	_cpu.CLR_NZV();
    	_cpu.SET_FLAGS8D(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d dec_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $OC INC direct -***- */
    public void inc_di()
    {
        int t=_cpu.DIRBYTE();
    	t=t+1 & 0xFF;
    	_cpu.CLR_NZV();
    	_cpu.SET_FLAGS8I(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d inc_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $OD TST direct -**0- */
    public void tst_di()
    {
        int t=_cpu.DIRBYTE();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d tst_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $0E JMP direct ----- */
    public void jmp_di()
    {
    	_cpu.DIRECT();
        _cpu._m6809.pc = (char)(_cpu.ea&0xFFFF);
        _cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d jmp_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $0F CLR direct -0100 */
    public void clr_di()
    {
    	_cpu.DIRECT();
    	_cpu.WM(_cpu.ea,0);
    	_cpu.CLR_NZVC();
    	_cpu.SEZ();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d clr_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $12 NOP inherent ----- */
    public void nop()
    {

    }
    /* $13 SYNC inherent ----- */
    public void sync()
    {
    	/* SYNC stops processing instructions until an interrupt request happens. */
    	/* This doesn't require the corresponding interrupt to be enabled: if it */
    	/* is disabled, execution continues with the next instruction. */
    	_cpu._m6809.int_state |= M6809_SYNC;	 /* HJB 990227 */
    	_cpu.CHECK_IRQ_LINES();
    	/* if M6809_SYNC has not been cleared by CHECK_IRQ_LINES,
    	 * stop execution until the interrupt lines change. */
    	if(( _cpu._m6809.int_state & M6809_SYNC )!=0)
    		if (_cpu.m6809_ICount[0] > 0) _cpu.m6809_ICount[0] = 0;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sync :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }

    public void lbra()//checked
    {      
       _cpu.ea=_cpu.IMMWORD();
       _cpu._m6809.pc = (char)((_cpu._m6809.pc + _cpu.ea));
       _cpu.CHANGE_PC();
    
    	if ( _cpu.ea == 0xfffd )  /* EHC 980508 speed up busy loop */
    		if ( _cpu.m6809_ICount[0] > 0)
   			_cpu.m6809_ICount[0] = 0;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbra :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $17 LBSR relative ----- */
    public void lbsr()//checked
    {
    	_cpu.ea=_cpu.IMMWORD();
    	_cpu.PUSHWORD(_cpu._m6809.pc);
    	_cpu._m6809.pc=(char)((_cpu._m6809.pc + _cpu.ea));
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbsr :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $19 DAA inherent (A) -**0* */
    public void daa()//suapicious recheck
    {
    	int/*UINT8*/ msn, lsn;
    	int/*UINT16*/ t, cf = 0;
    	msn = _cpu._m6809.a & 0xf0; 
        lsn = _cpu._m6809.a & 0x0f;
    	if( lsn>0x09 || (_cpu._m6809.cc & _cpu.CC_H)!=0) cf |= 0x06;
    	if( msn>0x80 && lsn>0x09 ) cf |= 0x60;
    	if( msn>0x90 || (_cpu._m6809.cc & _cpu.CC_C)!=0) cf |= 0x60;
    	t = cf + _cpu._m6809.a & 0xFFFF;//should be unsigned???
    	_cpu.CLR_NZV(); /* keep carry from previous operation */
    	_cpu.SET_NZ8(/*(UINT8)*/t & 0xFF); 
        _cpu.SET_C8(t);
    	_cpu._m6809.a = (char)(t & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d daa :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $1A ORCC immediate ##### */
    public void orcc()
    {
    	int t=	_cpu.IMMBYTE();
    	_cpu._m6809.cc |= t;
    	_cpu.CHECK_IRQ_LINES();	/* HJB 990116 */
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d orcc :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $1C ANDCC immediate ##### */
    public void andcc()
    {
        int t= _cpu.IMMBYTE();
    	_cpu._m6809.cc &= t;
    	_cpu.CHECK_IRQ_LINES();	/* HJB 990116 */
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d andcc :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $1D SEX inherent -**0- */
    public void sex()
    {
        int t = (byte)_cpu._m6809.b & 0xFFFF;
        setDreg(t);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $1E EXG inherent ----- */
    public void exg()
    {
    	/*UINT16*/int t1,t2;
    	/*UINT8*/int tb;
    
    	tb=_cpu.IMMBYTE();
    	if(( (tb^(tb>>>4)) & 0x08 )!=0)	/* HJB 990225: mixed 8/16 bit case? */
    	{
    		/* transfer $ff to both registers */
    		t1 = t2 = 0xff;
    	}
    	else
    	{
    		switch(tb>>>4) {
    			case  0: t1 = getDreg();  break;
    			case  1: t1 = _cpu._m6809.x;  break;
    			case  2: t1 = _cpu._m6809.y;  break;
    			case  3: t1 = _cpu._m6809.u;  break;
    			case  4: t1 = _cpu._m6809.s;  break;
    			case  5: t1 = _cpu._m6809.pc; break;
    			case  8: t1 = _cpu._m6809.a;  break;
    			case  9: t1 = _cpu._m6809.b;  break;
    			case 10: t1 = _cpu._m6809.cc; break;
    			case 11: t1 = _cpu._m6809.dp; break;
    			default: t1 = 0xff;
    		}
    		switch(tb&15) {
    			case  0: t2 = getDreg();  break;
    			case  1: t2 = _cpu._m6809.x;  break;
    			case  2: t2 = _cpu._m6809.y;  break;
    			case  3: t2 = _cpu._m6809.u;  break;
    			case  4: t2 = _cpu._m6809.s;  break;
    			case  5: t2 = _cpu._m6809.pc; break;
    			case  8: t2 = _cpu._m6809.a;  break;
    			case  9: t2 = _cpu._m6809.b;  break;
    			case 10: t2 = _cpu._m6809.cc; break;
    			case 11: t2 = _cpu._m6809.dp; break;
    			default: t2 = 0xff;
            }
    	}
    	switch(tb>>>4) {
    		case  0: setDreg(t2);  break;
    		case  1: _cpu._m6809.x = (char)(t2);  break;
    		case  2: _cpu._m6809.y = (char)(t2);  break;
    		case  3: _cpu._m6809.u = (char)(t2);  break;
    		case  4: _cpu._m6809.s = (char)(t2);  break;
    		case  5: _cpu._m6809.pc = (char)(t2); _cpu.CHANGE_PC(); break;
    		case  8: _cpu._m6809.a = (char)(t2);  break;
    		case  9: _cpu._m6809.b = (char)(t2);  break;
    		case 10: _cpu._m6809.cc= (char)(t2); break;
    		case 11: _cpu._m6809.dp = (char)(t2); break;
    	}
    	switch(tb&15) {
    		case  0: setDreg(t1);  break;
    		case  1: _cpu._m6809.x = (char)(t1);  break;
    		case  2: _cpu._m6809.y = (char)(t1);  break;
    		case  3: _cpu._m6809.u = (char)(t1);  break;
    		case  4: _cpu._m6809.s = (char)(t1);  break;
    		case  5: _cpu._m6809.pc = (char)(t1); _cpu.CHANGE_PC(); break;
    		case  8: _cpu._m6809.a = (char)(t1&0xFF);  break;
    		case  9: _cpu._m6809.b = (char)(t1&0xFF);  break;
    		case 10: _cpu._m6809.cc = (char)(t1&0xFF); break;
    		case 11: _cpu._m6809.dp = (char)(t1&0xFF); break;
    	}
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d exg :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $1F TFR inherent ----- */
    public void tfr()
    {
    	/*UINT8*/int tb;
    	/*UINT16*/ int t;
    
    	tb=_cpu.IMMBYTE();
    	if(( (tb^(tb>>>4)) & 0x08 )!=0)	/* HJB 990225: mixed 8/16 bit case? */
    	{
    		/* transfer $ff to register */
    		t = 0xff;
        }
    	else
    	{
    		switch(tb>>>4) {
    			case  0: t = getDreg();  break;
    			case  1: t = _cpu._m6809.x;  break;
    			case  2: t = _cpu._m6809.y;  break;
    			case  3: t = _cpu._m6809.u;  break;
    			case  4: t = _cpu._m6809.s;  break;
    			case  5: t = _cpu._m6809.pc; break;
    			case  8: t = _cpu._m6809.a;  break;
    			case  9: t = _cpu._m6809.b;  break;
    			case 10: t = _cpu._m6809.cc; break;
    			case 11: t = _cpu._m6809.dp; break;
    			default: t = 0xff;
            }
    	}
    	switch(tb&15) {
    		case  0: setDreg(t);   break;
    		case  1: _cpu._m6809.x = (char)(t);  break;
    		case  2: _cpu._m6809.y = (char)(t);  break;
    		case  3: _cpu._m6809.u = (char)(t);  break;
    		case  4: _cpu._m6809.s = (char)(t);  break;
    		case  5: _cpu._m6809.pc = (char)(t); _cpu.CHANGE_PC(); break;
    		case  8: _cpu._m6809.a = (char)(t&0xFF);  break;
    		case  9: _cpu._m6809.b = (char)(t&0xFF);  break;
    		case 10: _cpu._m6809.cc = (char)(t&0xFF); break;
    		case 11: _cpu._m6809.dp = (char)(t&0xFF); break;
        }
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d tfr :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }

    public void bra()//checked
    {
        int t;
        t=_cpu.IMMBYTE();
        _cpu._m6809.pc=(char)(_cpu._m6809.pc+(byte)t);//TODO check if it has to be better...
        _cpu.CHANGE_PC();
    	/* JB 970823 - speed up busy loops */
    	if( t == 0xfe )
    		if( _cpu.m6809_ICount[0] > 0 ) _cpu.m6809_ICount[0] = 0;
         //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bra :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }

    public void brn()//checked
    {
        int t=	_cpu.IMMBYTE();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d brn :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }

    public void lbrn()//checked
    {
       _cpu.ea=_cpu.IMMWORD();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbrn :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $22 BHI relative ----- */
    public void bhi()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc & (_cpu.CC_Z|_cpu.CC_C))==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bhi :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $1022 LBHI relative ----- */
    public void lbhi()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc & (_cpu.CC_Z|_cpu.CC_C))==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbhi :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $23 BLS relative ----- */
    public void bls()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc & (_cpu.CC_Z|_cpu.CC_C))!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bls :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $1023 LBLS relative ----- */
    public void lbls()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc & (_cpu.CC_Z|_cpu.CC_C))!=0 );
       if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbls :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $24 BCC relative ----- */
    public void bcc()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_C)==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bcc :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $1024 LBCC relative ----- */
    public void lbcc()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_C) ==0);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbcc :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $25 BCS relative ----- */
    public void bcs()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_C)!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bcs :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $1025 LBCS relative ----- */
    public void lbcs()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_C)!=0 );
       if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbcs :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $26 BNE relative ----- */
    public void bne()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_Z)==0 );
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bne :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $1026 LBNE relative ----- */
    public void lbne()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_Z)==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbne :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $27 BEQ relative ----- */
    public void beq()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_Z)!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d beq :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $1027 LBEQ relative ----- */
    public void lbeq()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_Z)!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbeq :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $28 BVC relative ----- */
    public void bvc()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_V)==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bvc :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $1028 LBVC relative ----- */
    public void lbvc()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_V)==0 );
    }
    /* $29 BVS relative ----- */
    public void bvs()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_V)!=0 );
    }
    /* $1029 LBVS relative ----- */
    public void lbvs()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_V)!=0 );
    }
    /* $2A BPL relative ----- */
    public void bpl()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_N)==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bpl :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $102A LBPL relative ----- */
    public void lbpl()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_N)==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbpl :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $2B BMI relative ----- */
    public void bmi()
    {
    	_cpu.BRANCH( (_cpu._m6809.cc&_cpu.CC_N)!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bmi :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $102B LBMI relative ----- */
    public void lbmi()
    {
    	_cpu.LBRANCH( (_cpu._m6809.cc&_cpu.CC_N)!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbmi :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $2C BGE relative ----- */
    public void bge()
    {
    	_cpu.BRANCH( _cpu.NXORV()==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bge :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    //* $102C LBGE relative ----- */
    public void lbge()
    {
    	_cpu.LBRANCH( _cpu.NXORV()==0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbge :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $2D BLT relative ----- */
    public void blt()
    {
    	_cpu.BRANCH( _cpu.NXORV()!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d blt :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $102D LBLT relative ----- */
    public void lblt()
    {
    	_cpu.LBRANCH( _cpu.NXORV()!=0 );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lblt :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $2E BGT relative ----- */
    public void bgt()
    {
    	_cpu.BRANCH( !((_cpu.NXORV()!=0) || ((_cpu._m6809.cc&_cpu.CC_Z)!=0)) );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bgt :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $102E LBGT relative ----- */
    public void lbgt()
    {
    	_cpu.LBRANCH( !((_cpu.NXORV()!=0) || ((_cpu._m6809.cc&_cpu.CC_Z)!=0)) );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lbgt :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $2F BLE relative ----- */
    public void ble()
    {
    	_cpu.BRANCH( (_cpu.NXORV()!=0 || (_cpu._m6809.cc&_cpu.CC_Z)!=0) );
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ble :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $102F LBLE relative ----- */
    public void lble()
    {
    	_cpu.LBRANCH( (_cpu.NXORV()!=0 || (_cpu._m6809.cc&_cpu.CC_Z)!=0) );
    }
    /* $30 LEAX indexed --*-- */
    public void leax()
    {
    	_cpu.fetch_effective_address();
        _cpu._m6809.x = (char)(_cpu.ea);
    	_cpu.CLR_Z();
    	_cpu.SET_Z(_cpu._m6809.x);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d leax :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $31 LEAY indexed --*-- */
    public void leay()
    {
    	_cpu.fetch_effective_address();
        _cpu._m6809.y = (char)(_cpu.ea);
    	_cpu.CLR_Z();
    	_cpu.SET_Z(_cpu._m6809.y);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d leay :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    
    }
    /* $32 LEAS indexed ----- */
    public void leas()
    {
    	_cpu.fetch_effective_address();
        _cpu._m6809.s = (char)(_cpu.ea);
    	_cpu._m6809.int_state |= M6809_LDS;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d leas :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $33 LEAU indexed ----- */
    public void leau()
    {
    	_cpu.fetch_effective_address();
        _cpu._m6809.u = (char)(_cpu.ea);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d leau :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $34 PSHS inherent ----- */
    public void pshs()
    {
    	int t=_cpu.IMMBYTE();
    	if((  t&0x80 )!=0) { _cpu.PUSHWORD(_cpu._m6809.pc); _cpu.m6809_ICount[0]-= 2; }
    	if((  t&0x40 )!=0) { _cpu.PUSHWORD(_cpu._m6809.u);  _cpu.m6809_ICount[0]-= 2; }
    	if((  t&0x20 )!=0) { _cpu.PUSHWORD(_cpu._m6809.y);  _cpu.m6809_ICount[0]-= 2; }
    	if((  t&0x10 )!=0) { _cpu.PUSHWORD(_cpu._m6809.x);  _cpu.m6809_ICount[0]-= 2; }
    	if((  t&0x08 )!=0) { _cpu.PUSHBYTE(_cpu._m6809.dp);  _cpu.m6809_ICount[0]-= 1; }
    	if((  t&0x04 )!=0) { _cpu.PUSHBYTE(_cpu._m6809.b);   _cpu.m6809_ICount[0]-= 1; }
    	if((  t&0x02 )!=0) { _cpu.PUSHBYTE(_cpu._m6809.a);   _cpu.m6809_ICount[0]-= 1; }
    	if((  t&0x01 )!=0) { _cpu.PUSHBYTE(_cpu._m6809.cc);  _cpu.m6809_ICount[0]-= 1; }
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d pshs :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
        
    }
    
    /* 35 PULS inherent ----- */
    public void puls()
    {
        int t=_cpu.IMMBYTE();
    	if(( t&0x01 )!=0) { _cpu._m6809.cc=(char)(_cpu.PULLBYTE()); _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x02 )!=0) { _cpu._m6809.a=(char)(_cpu.PULLBYTE());  _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x04 )!=0) { _cpu._m6809.b=(char)(_cpu.PULLBYTE());  _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x08 )!=0) { _cpu._m6809.dp=(char)(_cpu.PULLBYTE()); _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x10 )!=0) { _cpu._m6809.x=(char)(_cpu.PULLWORD()); _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x20 )!=0) { _cpu._m6809.y=(char)(_cpu.PULLWORD()); _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x40 )!=0) { _cpu._m6809.u=(char)(_cpu.PULLWORD()); _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x80 )!=0) { _cpu._m6809.pc=(char)(_cpu.PULLWORD()); _cpu.CHANGE_PC(); _cpu.m6809_ICount[0] -= 2; }
    
    	/* HJB 990225: moved check after all PULLs */
    	if(( t&0x01 )!=0) { _cpu.CHECK_IRQ_LINES(); }
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d puls :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $36 PSHU inherent ----- */
    public void pshu()
    {
    	int t=_cpu.IMMBYTE();
    	if(( t&0x80 )!=0) { _cpu.PSHUWORD(_cpu._m6809.pc); _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x40 )!=0) { _cpu.PSHUWORD(_cpu._m6809.s);  _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x20 )!=0) { _cpu.PSHUWORD(_cpu._m6809.y);  _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x10 )!=0) { _cpu.PSHUWORD(_cpu._m6809.x);  _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x08 )!=0) { _cpu.PSHUBYTE(_cpu._m6809.dp);  _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x04 )!=0) { _cpu.PSHUBYTE(_cpu._m6809.b);   _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x02 )!=0) { _cpu.PSHUBYTE(_cpu._m6809.a);   _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x01 )!=0) { _cpu.PSHUBYTE(_cpu._m6809.cc);  _cpu.m6809_ICount[0] -= 1; }
        
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d pshu :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    
    /* 37 PULU inherent ----- */
    public void pulu()
    {
    	int t=_cpu.IMMBYTE();
    	if(( t&0x01 )!=0) { _cpu._m6809.cc=(char)(_cpu.PULUBYTE()); _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x02 )!=0) { _cpu._m6809.a=(char)(_cpu.PULUBYTE());  _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x04 )!=0) { _cpu._m6809.b=(char)(_cpu.PULUBYTE());  _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x08 )!=0) { _cpu._m6809.dp=(char)(_cpu.PULUBYTE()); _cpu.m6809_ICount[0] -= 1; }
    	if(( t&0x10 )!=0) { _cpu._m6809.x=(char)(_cpu.PULUWORD()); _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x20 )!=0) { _cpu._m6809.y=(char)(_cpu.PULUWORD()); _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x40 )!=0) { _cpu._m6809.s=(char)(_cpu.PULUWORD()); _cpu.m6809_ICount[0] -= 2; }
    	if(( t&0x80 )!=0) { _cpu._m6809.pc=(char)(_cpu.PULUWORD()); _cpu.CHANGE_PC(); _cpu.m6809_ICount[0] -= 2; }
    
    	/* HJB 990225: moved check after all PULLs */
    	if(( t&0x01 )!=0) { _cpu.CHECK_IRQ_LINES(); }
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d pulu :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $39 RTS inherent ----- */
    public void rts()
    {
    	_cpu._m6809.pc=(char)(_cpu.PULLWORD());
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rts :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $3A ABX inherent ----- */
    public void abx()
    {
        _cpu._m6809.x=(char)(_cpu._m6809.x+_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d abx :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $3B RTI inherent ##### */
    public void rti()
    {
    	int t;
    	_cpu._m6809.cc=(char)(_cpu.PULLBYTE());
    	t = _cpu._m6809.cc & _cpu.CC_E;		/* HJB 990225: entire state saved? */
    	if(t!=0)
    	{
            _cpu.m6809_ICount[0] -= 9;
    		_cpu._m6809.a=(char)(_cpu.PULLBYTE());
    		_cpu._m6809.b=(char)(_cpu.PULLBYTE());
    		_cpu._m6809.dp=(char)(_cpu.PULLBYTE());
    		_cpu._m6809.x=(char)(_cpu.PULLWORD());
    		_cpu._m6809.y=(char)(_cpu.PULLWORD());
    		_cpu._m6809.u=(char)(_cpu.PULLWORD());
    	}
    	_cpu._m6809.pc=(char)(_cpu.PULLWORD());
    	_cpu.CHANGE_PC();
    	_cpu.CHECK_IRQ_LINES();	/* HJB 990116 */
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rti :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        

    }
    
    /* $3C CWAI inherent ----1 */
    public void cwai()
    {
    	int t =_cpu.IMMBYTE();
    	_cpu._m6809.cc &= t;
    	/*
         * CWAI stacks the entire machine_old state on the hardware stack,
         * then waits for an interrupt; when the interrupt is taken
         * later, the state is *not* saved again after CWAI.
         */
    	_cpu._m6809.cc |= _cpu.CC_E; 		/* HJB 990225: save entire state */
    	_cpu.PUSHWORD(_cpu._m6809.pc);
    	_cpu.PUSHWORD(_cpu._m6809.u);
    	_cpu.PUSHWORD(_cpu._m6809.y);
    	_cpu.PUSHWORD(_cpu._m6809.x);
    	_cpu.PUSHBYTE(_cpu._m6809.dp);
    	_cpu.PUSHBYTE(_cpu._m6809.b);
    	_cpu.PUSHBYTE(_cpu._m6809.a);
    	_cpu.PUSHBYTE(_cpu._m6809.cc);
    	_cpu._m6809.int_state |= _cpu.M6809_CWAI;	 /* HJB 990228 */
        _cpu.CHECK_IRQ_LINES();    /* HJB 990116 */
    	if(( _cpu._m6809.int_state & _cpu.M6809_CWAI )!=0)
    		if( _cpu.m6809_ICount[0] > 0 )
    			_cpu.m6809_ICount[0] = 0;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cwai :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        

    }
    /* $3D MUL inherent --*-@ */
    public void mul()
    {
        int t;
    	t = ((_cpu._m6809.a&0xff) * (_cpu._m6809.b&0xff)) & 0xFFFF;
    	_cpu.CLR_ZC(); 
        _cpu.SET_Z16(t); 
        if((t&0x80)!=0) _cpu.SEC();
    	setDreg(t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d mul :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
       
    }

    /* $3F SWI (SWI2 SWI3) absolute indirect ----- */
    public void swi()
    {
        _cpu._m6809.cc |= _cpu.CC_E; 			/* HJB 980225: save entire state */
    	_cpu.PUSHWORD(_cpu._m6809.ppc);
    	_cpu.PUSHWORD(_cpu._m6809.u);
    	_cpu.PUSHWORD(_cpu._m6809.y);
    	_cpu.PUSHWORD(_cpu._m6809.x);
    	_cpu.PUSHBYTE(_cpu._m6809.dp);
    	_cpu.PUSHBYTE(_cpu._m6809.b);
    	_cpu.PUSHBYTE(_cpu._m6809.a);
    	_cpu.PUSHBYTE(_cpu._m6809.cc);
        _cpu._m6809.cc |= _cpu.CC_IF | _cpu.CC_II;	/* inhibit FIRQ and IRQ */
        _cpu._m6809.pc=_cpu.RM16(0xfffa);
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d swi :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }

    /* $103F SWI2 absolute indirect ----- */
    public void swi2()
    {
        _cpu._m6809.cc |= _cpu.CC_E; 			/* HJB 980225: save entire state */
    	_cpu.PUSHWORD(_cpu._m6809.ppc);
    	_cpu.PUSHWORD(_cpu._m6809.u);
    	_cpu.PUSHWORD(_cpu._m6809.y);
    	_cpu.PUSHWORD(_cpu._m6809.x);
    	_cpu.PUSHBYTE(_cpu._m6809.dp);
    	_cpu.PUSHBYTE(_cpu._m6809.b);
    	_cpu.PUSHBYTE(_cpu._m6809.a);
        _cpu.PUSHBYTE(_cpu._m6809.cc);
    	_cpu._m6809.pc = _cpu.RM16(0xfff4);
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d swi2 :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /*TODO*///
    /*TODO*////* $113F SWI3 absolute indirect ----- */
    public void swi3()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	CC |= CC_E; 			/* HJB 980225: save entire state */
    /*TODO*///	_cpu.PUSHWORD(pPC);
    /*TODO*///	_cpu.PUSHWORD(pU);
    /*TODO*///	_cpu.PUSHWORD(pY);
    /*TODO*///	_cpu.PUSHWORD(pX);
    /*TODO*///	PUSHBYTE(DP);
    /*TODO*///	PUSHBYTE(B);
    /*TODO*///	PUSHBYTE(A);
    /*TODO*///    PUSHBYTE(CC);
    /*TODO*///	PCD = RM16(0xfff2);
    /*TODO*///	CHANGE_PC;
    }
    /* $40 NEGA inherent ?**** */
    public void nega()
    {
        int r;
    	r = -_cpu._m6809.a & 0xFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(0,_cpu._m6809.a,r);
    	_cpu._m6809.a = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d nega :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $43 COMA inherent -**01 */
    public void coma()
    {
    	_cpu._m6809.a =(char)( ~_cpu._m6809.a & 0xFF);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
    	_cpu.SEC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d coma :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $44 LSRA inherent -0*-* */
    public void lsra()//suspicious recheck
    {
        _cpu.CLR_NZC();
    	_cpu._m6809.cc |= (_cpu._m6809.a & _cpu.CC_C);
    	_cpu._m6809.a = (char)(_cpu._m6809.a >>> 1 & 0xFF);
    	_cpu.SET_Z8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lsra :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $46 RORA inherent -**-* */
    public void rora()
    {
        int r;
    	r = ((_cpu._m6809.cc & _cpu.CC_C) << 7)&0xFF;
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (_cpu._m6809.a & _cpu.CC_C);
    	r = (r | _cpu._m6809.a >>> 1)&0xFF;
    	_cpu.SET_NZ8(r);
    	_cpu._m6809.a = (char)(r&0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rora :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $47 ASRA inherent ?**-* */
    public void asra()//suspicious recheck
    {
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (_cpu._m6809.a & _cpu.CC_C);
    	_cpu._m6809.a = (char)(((_cpu._m6809.a & 0x80) | (_cpu._m6809.a >>> 1)) &0xFF);
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asra :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $48 ASLA inherent ?**** */
    public void asla()
    {
        int r = (_cpu._m6809.a << 1) & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,_cpu._m6809.a,r);
    	_cpu._m6809.a = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asla :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $49 ROLA inherent -**** */
    public void rola()//very suspicious to recheck
    {
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rola(before):PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
 //BUGGY have to figure it out!
        int t,r;
    	t = _cpu._m6809.a;
   	r = ((_cpu._m6809.cc & _cpu.CC_C) | ((t<<1))) &0xFFFF;//is that correct???
    	_cpu.CLR_NZVC(); 
        _cpu.SET_FLAGS8(t,t,r);
    	_cpu._m6809.a = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rola:PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $4A DECA inherent -***- */
    public void deca()
    {
        _cpu._m6809.a = (char)(_cpu._m6809.a -1 & 0xFF);
    	_cpu.CLR_NZV();
    	_cpu.SET_FLAGS8D(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d deca :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);    
    }
    /* $4C INCA inherent -***- */
    public void inca()
    {
        _cpu._m6809.a = (char)(_cpu._m6809.a +1 & 0xFF);
    	_cpu.CLR_NZV();
    	_cpu.SET_FLAGS8I(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d inca :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $4D TSTA inherent -**0- */
    public void tsta()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
    }
    /* $4F CLRA inherent -0100 */
    public void clra()
    {
        _cpu._m6809.a = 0;
    	_cpu.CLR_NZVC(); 
        _cpu.SEZ();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d clra :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $50 NEGB inherent ?**** */
    public void negb()
    {
        int r;
    	r = -_cpu._m6809.b & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(0,_cpu._m6809.b,r);
    	_cpu._m6809.b = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d negb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $53 COMB inherent -**01 */
    public void comb()
    {
        _cpu._m6809.b = (char)((~_cpu._m6809.b) & 0xFF);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
    	_cpu.SEC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d comb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }   
    /* $54 LSRB inherent -0*-* */
    public void lsrb()
    {
        _cpu.CLR_NZC();
    	_cpu._m6809.cc |= (_cpu._m6809.b & _cpu.CC_C);
    	_cpu._m6809.b = (char)(_cpu._m6809.b >>> 1 &0xFF);
    	_cpu.SET_Z8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lsrb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);    
    }
    /* $56 RORB inherent -**-* */
    public void rorb()
    {
        int r;
    	r = ((_cpu._m6809.cc & _cpu.CC_C) << 7)&0xFF;
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (_cpu._m6809.b & _cpu.CC_C);
    	r = (r | _cpu._m6809.b >>> 1)&0xFF;
    	_cpu.SET_NZ8(r);
    	_cpu._m6809.b = (char)(r&0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rorb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);    
    }
    /* $57 ASRB inherent ?**-* */
    public void asrb()//suspicious recheck
    {
        _cpu.CLR_NZC();
    	_cpu._m6809.cc |= (_cpu._m6809.b & _cpu.CC_C);
    	_cpu._m6809.b = (char)(((_cpu._m6809.b & 0x80) | (_cpu._m6809.b >>> 1)) &0xFF);
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asrb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $58 ASLB inherent ?**** */
    public void aslb()
    {
        int r = (_cpu._m6809.b << 1) & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,_cpu._m6809.b,r);
    	_cpu._m6809.b = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d aslb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $59 ROLB inherent -**** */
    public void rolb()
    {
        int t,r;
    	t = _cpu._m6809.b;
    	r = _cpu._m6809.cc & _cpu.CC_C;
    	r = (r | t << 1) &0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(t,t,r);
    	_cpu._m6809.b = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rolb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $5A DECB inherent -***- */
    public void decb()
    {
       _cpu._m6809.b = (char)(_cpu._m6809.b-1&0xFF);
       _cpu.CLR_NZV();
       _cpu.SET_FLAGS8D(_cpu._m6809.b);
       if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d decb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $5C INCB inherent -***- */
    public void incb()
    {
        _cpu._m6809.b = (char)(_cpu._m6809.b +1 & 0xFF);
    	_cpu.CLR_NZV();
    	_cpu.SET_FLAGS8I(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d incb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $5D TSTB inherent -**0- */
    public void tstb()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
    }
    /* $5F CLRB inherent -0100 */
    public void clrb()
    {
    	_cpu._m6809.b = 0;
    	_cpu.CLR_NZVC(); _cpu.SEZ();
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d clrb :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $60 NEG indexed ?**** */
    public void neg_ix()
    {
        int r,t;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r=-t & 0xFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(0,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d neg_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $63 COM indexed -**01 */
    public void com_ix()
    {
        int t;
    	_cpu.fetch_effective_address();
    	t = ~_cpu.RM(_cpu.ea) & 0xFF;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(t);
    	_cpu.SEC();
    	_cpu.WM(_cpu.ea,t);
       if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d com_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $64 LSR indexed -0*-* */
    public void lsr_ix()
    {
        int t;
    	_cpu.fetch_effective_address();
    	t=_cpu.RM(_cpu.ea);
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (t & _cpu.CC_C);
    	t= t >>>1 & 0xFF;
        _cpu.SET_Z8(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lsr_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $66 ROR indexed -**-* */
    public void ror_ix()//suspicious recheck
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t=_cpu.RM(_cpu.ea);
    	r = (_cpu._m6809.cc & _cpu.CC_C) << 7 &0xFF;
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (t & _cpu.CC_C);
    	r = r | t>>>1 &0xFF;//correct???//r |= t>>1;
        _cpu.SET_NZ8(r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ror_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $67 ASR indexed ?**-* */
    public void asr_ix()
    {
        int t;
    	_cpu.fetch_effective_address();
    	t=_cpu.RM(_cpu.ea);
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (t & _cpu.CC_C);
    	t=((t&0x80)|(t>>>1))&0xFF;
    	_cpu.SET_NZ8(t);
   	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asr_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $68 ASL indexed ?**** */
    public void asl_ix()
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t=_cpu.RM(_cpu.ea);
   	r = (t << 1) &0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(t,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asl_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $69 ROL indexed -**** */
    public void rol_ix()//suspicious recheck
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t=_cpu.RM(_cpu.ea);
    	r = _cpu._m6809.cc & _cpu.CC_C;
    	r = (r | t << 1) & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(t,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rol_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $6A DEC indexed -***- */
    public void dec_ix()
    {
        int t;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea) - 1 & 0xFF;
    	_cpu.CLR_NZV();
        _cpu.SET_FLAGS8D(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d dec_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    
    /*TODO*////* $6C INC indexed -***- */
    public void inc_ix()
    {
        int t;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea) + 1 &0xFF;
    	_cpu.CLR_NZV(); 
        _cpu.SET_FLAGS8I(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d inc_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $6D TST indexed -**0- */
    public void tst_ix()
    {
        int t;
   	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d tst_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $6E JMP indexed ----- */
    public void jmp_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.pc = (char)(_cpu.ea);
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d jmp_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $6F CLR indexed -0100 */
    public void clr_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.WM(_cpu.ea,0);
    	_cpu.CLR_NZVC(); 
        _cpu.SEZ();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d clr_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $70 NEG extended ?**** */
    public void neg_ex()
    {
        int r,t;
    	t=_cpu.EXTBYTE();
    	r=-t & 0xFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(0,t,r);
    	_cpu.WM(_cpu.ea,r);
    }
    /* $73 COM extended -**01 */
    public void com_ex()
    {
        int t= _cpu.EXTBYTE(); 
        t = ~t & 0xFF;
    	_cpu.CLR_NZV(); 
        _cpu.SET_NZ8(t); 
        _cpu.SEC();
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d com_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $74 LSR extended -0*-* */
    public void lsr_ex()
    {
        int t=_cpu.EXTBYTE(); 
        _cpu.CLR_NZC(); 
        _cpu._m6809.cc |= (t & _cpu.CC_C);
    	t=t>>>1 &0XFF;
        _cpu.SET_Z8(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lsr_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $76 ROR extended -**-* */
    public void ror_ex()
    {
        int t,r;
    	t=_cpu.EXTBYTE();
        r=((_cpu._m6809.cc & _cpu.CC_C) << 7)&0xFF;
    	_cpu.CLR_NZC();
        _cpu._m6809.cc |= (t & _cpu.CC_C);
    	r = (r| t>>>1)&0xFF;
        _cpu.SET_NZ8(r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ror_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        

    }
    /* $77 ASR extended ?**-* */
    public void asr_ex()
    {
        int t=_cpu.EXTBYTE();
    	_cpu.CLR_NZC();
    	_cpu._m6809.cc |= (t & _cpu.CC_C);
    	t = ((t & 0x80) | (t >>> 1))&0xFF;
    	_cpu.SET_NZ8(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asr_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $78 ASL extended ?**** */
    public void asl_ex()
    {
        int t,r;
        t= _cpu.EXTBYTE();
        r=t<<1 & 0xFFFF;
        _cpu.CLR_NZVC(); 
        _cpu.SET_FLAGS8(t,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d asl_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        

    }
    /* $79 ROL extended -**** */
    public void rol_ex()
    {
        int t,r;
        t= _cpu.EXTBYTE();
        r = ((_cpu._m6809.cc & _cpu.CC_C) | (t << 1))&0xFFFF;
    	_cpu.CLR_NZVC(); 
        _cpu.SET_FLAGS8(t,t,r);
    	_cpu.WM(_cpu.ea,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d rol_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $7A DEC extended -***- */
    public void dec_ex()
    {
       int t=_cpu.EXTBYTE(); 
       t=t-1&0xFF;
       _cpu.CLR_NZV(); 
       _cpu.SET_FLAGS8D(t);
       _cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d dec_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $7C INC extended -***- */
    public void inc_ex()
    {
        int t=_cpu.EXTBYTE(); 
        t=t+1&0xFF;
    	_cpu.CLR_NZV(); 
        _cpu.SET_FLAGS8I(t);
    	_cpu.WM(_cpu.ea,t);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d inc_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $7D TST extended -**0- */
    public void tst_ex()
    {
    	int t=_cpu.EXTBYTE(); 
        _cpu.CLR_NZV(); 
        _cpu.SET_NZ8(t);
    }
    /* $7E JMP extended ----- */
    public void jmp_ex()
    {
    	_cpu.EXTENDED();
    	_cpu._m6809.pc = (char)(_cpu.ea);
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d jmp_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $7F CLR extended -0100 */
    public void clr_ex()
    {
    	_cpu.EXTENDED();
    	_cpu.WM(_cpu.ea,0);
    	_cpu.CLR_NZVC(); 
        _cpu.SEZ();
    }
    /* $80 SUBA immediate ?**** */
    public void suba_im()
    {
        int t,r;
        t=_cpu.IMMBYTE();
    	r = _cpu._m6809.a - t & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d suba_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);  
    }
    /* $81 CMPA immediate ?**** */
    public void cmpa_im()
    {
       int t,r;
       t=_cpu.IMMBYTE();
       r = (_cpu._m6809.a - t) & 0xFFFF;
       _cpu.CLR_NZVC();
       _cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
       if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpa_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $82 SBCA immediate ?**** */
    public void sbca_im()
    {
        int t,r;
        t=_cpu.IMMBYTE();
    	r = (_cpu._m6809.a - t - (_cpu._m6809.cc & _cpu.CC_C))& 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbca_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);  
    
    }
    /* $83 SUBD (CMPD CMPU) immediate -**** */
    public void subd_im()
    {
       int r,d;
       int b=_cpu.IMMWORD();
    	d = getDreg();
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subd_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);  
    }
    /* $1083 CMPD immediate -**** */
    public void cmpd_im()
    {
        int r,d;
        int b=_cpu.IMMWORD();
    	d = getDreg();
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpd_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $1183 CMPU immediate -**** */
    public void cmpu_im()
    {
        int r, d;
        int b=_cpu.IMMWORD();
    	d = _cpu._m6809.u;
    	r = (d - b); //& 0xFFFF;//should be unsigned?
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpu_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $84 ANDA immediate -**0- */
    public void anda_im()//suspicious recheck
    {
        int t=_cpu.IMMBYTE();
    	_cpu._m6809.a &= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d anda_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $85 BITA immediate -**0- */
    public void bita_im()//suspicious recheck
    {
   	int t,r;
        t=_cpu.IMMBYTE();
    	r = _cpu._m6809.a & t;
       	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bita_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $86 LDA immediate -**0- */
    public void lda_im()
    {
    	_cpu._m6809.a=_cpu.IMMBYTE();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lda_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /*TODO*///
    /*TODO*////* is this a legal instruction? */
    /*TODO*////* $87 STA immediate -**0- */
    public void sta_im()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	_cpu.CLR_NZV;
    /*TODO*///	_cpu.SET_NZ8(A);
    /*TODO*///	IMM8;
    /*TODO*///	WM(EAD,A);
    }
    //* $88 EORA immediate -**0- */
    public void eora_im()
    {
        int t=_cpu.IMMBYTE();
    	_cpu._m6809.a ^= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d eora_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $89 ADCA immediate ***** */
    public void adca_im()
    {
        int t,r;
    	t=_cpu.IMMBYTE();
    	r = _cpu._m6809.a + t + (_cpu._m6809.cc & _cpu.CC_C) & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r & 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adca_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $8A ORA immediate -**0- */
    public void ora_im()
    {
        int t=_cpu.IMMBYTE();
    	_cpu._m6809.a |= t; //TODO should unsigned it??
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ora_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
       
    }
    /* $8B ADDA immediate ***** */
    public void adda_im()
    {
        int t,r;
        t=_cpu.IMMBYTE();
    	r = _cpu._m6809.a + t & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adda_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $8C CMPX (CMPY CMPS) immediate -**** */
    public void cmpx_im()//suspicious recheck it
    {
        int r,d;
        int b=_cpu.IMMWORD();
	d = _cpu._m6809.x;
    	r = (d - b); //&0xFFFF;//should be unsigned?
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpx_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
       
    }
    /* $108C CMPY immediate -**** */
    public void cmpy_im()
    {
        int r,d;
        int b=_cpu.IMMWORD();
	d = _cpu._m6809.y;
    	r = (d - b); //&0xFFFF;//should be unsigned?
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpy_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $118C CMPS immediate -**** */
    public void cmps_im()
    {
        int r,d;
        int b=_cpu.IMMWORD();
	d = _cpu._m6809.s;
    	r = (d - b); //&0xFFFF;//should be unsigned?
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmps_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $8D BSR ----- */
    public void bsr()
    {
    	int t=_cpu.IMMBYTE();
    	_cpu.PUSHWORD(_cpu._m6809.pc);
    	_cpu._m6809.pc = (char)(_cpu._m6809.pc + (byte)t);
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bsr :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $8E LDX (LDY) immediate -**0- */
    public void ldx_im()
    {
    	_cpu._m6809.x=(char)(_cpu.IMMWORD());
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.x);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldx_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $108E LDY immediate -**0- */
    public void ldy_im()
    {
        _cpu._m6809.y=(char)(_cpu.IMMWORD());
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.y);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldy_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea); 
    }
    /*TODO*///
    /*TODO*////* is this a legal instruction? */
    /*TODO*////* $8F STX (STY) immediate -**0- */
    public void stx_im()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	_cpu.CLR_NZV;
    /*TODO*///	_cpu.SET_NZ16(X);
    /*TODO*///	IMM16;
    /*TODO*///	WM16(EAD,&pX);
    }
    /*TODO*///
    /*TODO*////* is this a legal instruction? */
    /*TODO*////* $108F STY immediate -**0- */
    public void sty_im()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	_cpu.CLR_NZV;
    /*TODO*///	_cpu.SET_NZ16(Y);
    /*TODO*///	IMM16;
    /*TODO*///	WM16(EAD,&pY);
    }
    /* $90 SUBA direct ?**** */
    public void suba_di()
    {
        int t,r;
        t= _cpu.DIRBYTE();
    	r = _cpu._m6809.a - t & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d suba_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $91 CMPA direct ?**** */
    public void cmpa_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = _cpu._m6809.a - t &0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpa_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $92 SBCA direct ?**** */
    public void sbca_di()//suspicious recheck
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = (_cpu._m6809.a - t - (_cpu._m6809.cc & _cpu.CC_C)) &0xFFFF;//should be unsigned??
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbca_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $93 SUBD (CMPD CMPU) direct -**** */
    public void subd_di()
    {
        int r,d;
        int b=_cpu.DIRWORD();
    	d = getDreg();
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
   	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subd_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    //* $1093 CMPD direct -**** */
    public void cmpd_di()
    {
        int r,d;
        int b;
    	b=_cpu.DIRWORD();
    	d = getDreg();
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpd_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $1193 CMPU direct -**** */
    public void cmpu_di()
    {
        int r,d;
    	int b=_cpu.DIRWORD();
    	d = _cpu._m6809.u;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpu_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $94 ANDA direct -**0- */
    public void anda_di()
    {
    	int t=_cpu.DIRBYTE();
    	_cpu._m6809.a &= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d anda_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $95 BITA direct -**0- */
    public void bita_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = _cpu._m6809.a & t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bita_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $96 LDA direct -**0- */
    public void lda_di()
    {
    	_cpu._m6809.a=_cpu.DIRBYTE();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lda_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $97 STA direct -**0- */
    public void sta_di()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
    	_cpu.DIRECT();
    	_cpu.WM(_cpu.ea,_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sta_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $98 EORA direct -**0- */
    public void eora_di()
    {
        int t=_cpu.DIRBYTE();
    	_cpu._m6809.a ^= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d eora_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $99 ADCA direct ***** */
    public void adca_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = (_cpu._m6809.a + t + (_cpu._m6809.cc & _cpu.CC_C)) & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a= (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adca_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /*TODO*///
    /*TODO*////* $9A ORA direct -**0- */
    public void ora_di()
    {
        int t= _cpu.DIRBYTE();
    	_cpu._m6809.a |= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ora_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $9B ADDA direct ***** */
    public void adda_di()
    {
   	int t,r;
        t=_cpu.DIRBYTE();
    	r = _cpu._m6809.a + t;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adda_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $9C CMPX (CMPY CMPS) direct -**** */
    public void cmpx_di()
    {
        int r,d;
    	int b=_cpu.DIRWORD();
    	d = _cpu._m6809.x;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpx_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $109C CMPY direct -**** */
    public void cmpy_di()
    {
        int r,d;
    	int b=_cpu.DIRWORD();
    	d = _cpu._m6809.y;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpy_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $119C CMPS direct -**** */
    public void cmps_di()
    {
        int r,d;
        int b=_cpu.DIRWORD();
        d = _cpu._m6809.s;
        r = d - b;
        _cpu.CLR_NZVC();
        _cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmps_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $9D JSR direct ----- */
    public void jsr_di()
    {
        _cpu.DIRECT();
        _cpu.PUSHWORD(_cpu._m6809.pc);
        _cpu._m6809.pc = (char)(_cpu.ea);
        _cpu.CHANGE_PC();
    }
    /* $9E LDX (LDY) direct -**0- */
    public void ldx_di()
    {
    	_cpu._m6809.x=(char)(_cpu.DIRWORD());
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.x);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldx_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $109E LDY direct -**0- */
    public void ldy_di()
    {
        _cpu._m6809.y=(char)(_cpu.DIRWORD());
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.y);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldy_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $9F STX (STY) direct -**0- */
    public void stx_di()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.x);
    	_cpu.DIRECT();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.x);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stx_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $109F STY direct -**0- */
    public void sty_di()
    {
        _cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.y);
    	_cpu.DIRECT();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.y);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sty_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $a0 SUBA indexed ?**** */
    public void suba_ix()
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = _cpu._m6809.a - t & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d suba_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $a1 CMPA indexed ?**** */
    public void cmpa_ix()
    {
    	/*UINT16*/int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = (_cpu._m6809.a - t) &0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpa_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $a2 SBCA indexed ?**** */
    public void sbca_ix()
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = (_cpu._m6809.a - t - (_cpu._m6809.cc & _cpu.CC_C))&0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbca_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $a3 SUBD (CMPD CMPU) indexed -**** */
    public void subd_ix()
    {
        int r,d;
        int b;
    	_cpu.fetch_effective_address();
        b=_cpu.RM16(_cpu.ea);
    	d = getDreg();
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subd_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $10a3 CMPD indexed -**** */
    public void cmpd_ix()
    {
        int r,d;
        int b;
    	_cpu.fetch_effective_address();
        b=_cpu.RM16(_cpu.ea);
    	d = getDreg();
    	r = (d -  b); //& 0xFFFF; //should be unsinged?????
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);

        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpd_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $11a3 CMPU indexed -**** */
    public void cmpu_ix()
    {    
        int r;
        int b;
    	_cpu.fetch_effective_address();
        b=_cpu.RM16(_cpu.ea);
    	r = _cpu._m6809.u - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(_cpu._m6809.u,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpu_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $a4 ANDA indexed -**0- */
    public void anda_ix()
    {
        _cpu.fetch_effective_address();
    	_cpu._m6809.a &= _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d anda_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $a5 BITA indexed -**0- */
    public void bita_ix()
    {
        int r;
    	_cpu.fetch_effective_address();
    	r = _cpu._m6809.a & _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bita_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $a6 LDA indexed -**0- */
    public void lda_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.a = _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lda_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $a7 STA indexed -**0- */
    public void sta_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
    	_cpu.WM(_cpu.ea,_cpu._m6809.a);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sta_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $a8 EORA indexed -**0- */
    public void eora_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.a ^= _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d eora_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $a9 ADCA indexed ***** */
    public void adca_ix()
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = _cpu._m6809.a + t + (_cpu._m6809.cc & _cpu.CC_C) & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adca_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $aA ORA indexed -**0- */
    public void ora_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.a |= _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ora_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $aB ADDA indexed ***** */
    public void adda_ix()
    {
       
    	int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = _cpu._m6809.a + t &0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adda_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    
    }
    /* $aC CMPX (CMPY CMPS) indexed -**** */
    public void cmpx_ix()
    {
    	int r,d;
    	int b;
    	_cpu.fetch_effective_address();
        b=_cpu.RM16(_cpu.ea);
    	d = _cpu._m6809.x;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpx_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    
    }
    /* $10aC CMPY indexed -**** */
    public void cmpy_ix()
    {
        int r,d;
    	int b;
    	_cpu.fetch_effective_address();
        b=_cpu.RM16(_cpu.ea);
    	d = _cpu._m6809.y;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpy_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);  
    }
    /* $11aC CMPS indexed -**** */
    public void cmps_ix()
    {
        int r,d;
        int b;
        _cpu.fetch_effective_address();
        b=_cpu.RM16(_cpu.ea);
        d = _cpu._m6809.s;
        r = d - b;
        _cpu.CLR_NZVC();
        _cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmps_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $aD JSR indexed ----- */
    public void jsr_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.PUSHWORD(_cpu._m6809.pc);
    	_cpu._m6809.pc = (char)(_cpu.ea);
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d jsr_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $aE LDX (LDY) indexed -**0- */
    public void ldx_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu._m6809.x=(char)(_cpu.RM16(_cpu.ea));
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.x);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldx_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $10aE LDY indexed -**0- */
    public void ldy_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu._m6809.y=(char)(_cpu.RM16(_cpu.ea));
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.y);
         //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldy_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $aF STX (STY) indexed -**0- */
    public void stx_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.x);
    	_cpu.WM16(_cpu.ea,_cpu._m6809.x);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stx_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $10aF STY indexed -**0- */
    public void sty_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.y);
    	_cpu.WM16(_cpu.ea,_cpu._m6809.y);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sty_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $b0 SUBA extended ?**** */
    public void suba_ex()
    {
        int t,r;
    	t=_cpu.EXTBYTE();
    	r = _cpu._m6809.a - t & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d suba_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $b1 CMPA extended ?**** */
    public void cmpa_ex()
    {
        int t,r;
    	t=_cpu.EXTBYTE();
    	r = _cpu._m6809.a - t;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpa_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $b2 SBCA extended ?**** */
    public void sbca_ex()
    {
        int  t,r;
        t=_cpu.EXTBYTE();
        r = (_cpu._m6809.a - t - (_cpu._m6809.cc & _cpu.CC_C))&0xFFFF;
        _cpu.CLR_NZVC();
        _cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
        _cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbca_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $b3 SUBD (CMPD CMPU) extended -**** */
    public void subd_ex()
    {
        int r,d;
        int b=_cpu.EXTWORD();
        d = getDreg();
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subd_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $10b3 CMPD extended -**** */
    public void cmpd_ex()
    {
        int r,d;
        int b=_cpu.EXTWORD();
    	d = getDreg();
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpd_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $11b3 CMPU extended -**** */
    public void cmpu_ex()
    {
        int r,d;
    	int b=_cpu.EXTWORD();
    	d = _cpu._m6809.u;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpu_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }  
    /* $b4 ANDA extended -**0- */
    public void anda_ex()
    {
        int t=_cpu.EXTBYTE();
    	_cpu._m6809.a &= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d anda_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $b5 BITA extended -**0- */
    public void bita_ex()
    {
        int t,r;
        t =_cpu.EXTBYTE();
    	r = _cpu._m6809.a & t;
    	_cpu.CLR_NZV(); 
        _cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bita_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $b6 LDA extended -**0- */
    public void lda_ex()
    {
    	_cpu._m6809.a=_cpu.EXTBYTE();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lda_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }

    /* $b7 STA extended -**0- */
    public void sta_ex()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
    	_cpu.EXTENDED();
    	_cpu.WM(_cpu.ea,_cpu._m6809.a);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sta_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $b8 EORA extended -**0- */
    public void eora_ex()
    {
        int t=	_cpu.EXTBYTE();
    	_cpu._m6809.a ^= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d eora_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $b9 ADCA extended ***** */
    public void adca_ex()
    {
        int  t,r;
    	t=_cpu.EXTBYTE();
    	r = (_cpu._m6809.a + t + (_cpu._m6809.cc & _cpu.CC_C)) & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
    }
    /* $bA ORA extended -**0- */
    public void ora_ex()
    {
        int t=_cpu.EXTBYTE();
    	_cpu._m6809.a |= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.a);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ora_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $bB ADDA extended ***** */
    public void adda_ex()
    {
        int  t,r;
    	t=_cpu.EXTBYTE();
    	r = _cpu._m6809.a + t & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.a,t,r);
    	_cpu.SET_H(_cpu._m6809.a,t,r);
    	_cpu._m6809.a = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adda_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $bC CMPX (CMPY CMPS) extended -**** */
    public void cmpx_ex()
    {
        int r,d;
    	int b=_cpu.EXTWORD();
    	d = _cpu._m6809.x;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpx_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $10bC CMPY extended -**** */
    public void cmpy_ex()
    {
    	int r,d;
    	int b=_cpu.EXTWORD();
    	d = _cpu._m6809.y;
    	r = d - b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpy_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $11bC CMPS extended -**** */
    public void cmps_ex()
    {
        int r,d;
        int b=_cpu.EXTWORD();
        d = _cpu._m6809.s;
        r = d - b;
        _cpu.CLR_NZVC();
        _cpu.SET_FLAGS16(d,b,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmps_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $bD JSR extended ----- */
    public void jsr_ex()
    {
    	_cpu.EXTENDED();
    	_cpu.PUSHWORD(_cpu._m6809.pc);
    	_cpu._m6809.pc = (char)(_cpu.ea);
    	_cpu.CHANGE_PC();
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d jsr_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        

    }
    /* $bE LDX (LDY) extended -**0- */
    public void ldx_ex()
    {
        _cpu._m6809.x=_cpu.EXTWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.x);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldx_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
     
    }
    /* $10bE LDY extended -**0- */
    public void ldy_ex()
    {
    	_cpu._m6809.y=_cpu.EXTWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.y);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldy_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
     
    }
    /* $bF STX (STY) extended -**0- */
    public void stx_ex()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.x);
    	_cpu.EXTENDED();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.x);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stx_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
     
    }
    /* $10bF STY extended -**0- */
    public void sty_ex()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.y);
    	_cpu.EXTENDED();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.y);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sty_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $c0 SUBB immediate ?**** */
    public void subb_im()
    {
        int t,r;
        t=_cpu.IMMBYTE();
    	r = _cpu._m6809.b - t & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);
         if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $c1 CMPB immediate ?**** */
    public void cmpb_im()
    {
        int t,r;
       t=_cpu.IMMBYTE();
       r = (_cpu._m6809.b - t) & 0xFFFF;
       _cpu.CLR_NZVC();
       _cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
       if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        

    }
    /* $c2 SBCB immediate ?**** */
    public void sbcb_im()
    {
        int t,r;
        t=_cpu.IMMBYTE();
        r = (_cpu._m6809.b - t - (_cpu._m6809.cc & _cpu.CC_C))& 0xFFFF;
        _cpu.CLR_NZVC();
        _cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
        _cpu._m6809.b = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbcb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $c3 ADDD immediate -**** */
    public void addd_im()
    {
        int r,d;
        int b=_cpu.IMMWORD();
    	d = getDreg();
    	r = d + b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addd_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $c4 ANDB immediate -**0- */
    public void andb_im()
    {
        int t=_cpu.IMMBYTE();
    	_cpu._m6809.b &= t;//should be unsigned?
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d andb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $c5 BITB immediate -**0- */
    public void bitb_im()
    {
        int t,r;
        t=_cpu.IMMBYTE();
    	r = _cpu._m6809.b & t;
       	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bitb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $c6 LDB immediate -**0- */
    public void ldb_im()
    {
        _cpu._m6809.b=_cpu.IMMBYTE();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /*TODO*///
    /*TODO*////* is this a legal instruction? */
    /*TODO*////* $c7 STB immediate -**0- */
    public void stb_im()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	_cpu.CLR_NZV;
    /*TODO*///	_cpu.SET_NZ8(B);
    /*TODO*///	IMM8;
    /*TODO*///	WM(EAD,B);
    }

    /* $c8 EORB immediate -**0- */
    public void eorb_im()
    {
        int t=_cpu.IMMBYTE();
    	_cpu._m6809.b ^= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d eorb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $c9 ADCB immediate ***** */
    public void adcb_im()
    {
        int t,r;
    	t=_cpu.IMMBYTE();
    	r = _cpu._m6809.b + t + (_cpu._m6809.cc & _cpu.CC_C) & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu.SET_H(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adcb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $cA ORB immediate -**0- */
    public void orb_im()
    {
        int t=_cpu.IMMBYTE();
    	_cpu._m6809.b |= t; //TODO should unsigned it??
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d orb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $cB ADDB immediate ***** */
    public void addb_im()
    {
        int t,r;
        t=_cpu.IMMBYTE();
    	r = _cpu._m6809.b + t & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu.SET_H(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
       if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addb_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $cC LDD immediate -**0- */
    public void ldd_im()
    {
    	int temp=_cpu.IMMWORD();
        setDreg(temp);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(temp);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldd_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /*TODO*///
    /*TODO*////* is this a legal instruction? */
    /*TODO*////* $cD STD immediate -**0- */
    public void std_im()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	_cpu.CLR_NZV;
    /*TODO*///	_cpu.SET_NZ16(D);
    /*TODO*///    IMM16;
    /*TODO*///	WM16(EAD,&pD);
    }
    /* $cE LDU (LDS) immediate -**0- */
    public void ldu_im()
    {
    	_cpu._m6809.u=_cpu.IMMWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.u);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldu_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);  
    }
    /* $10cE LDS immediate -**0- */
    public void lds_im()
    {
    	_cpu._m6809.s=_cpu.IMMWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.s);
    	_cpu._m6809.int_state |= M6809_LDS;
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d lds_im :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /*TODO*///
    /*TODO*////* is this a legal instruction? */
    /*TODO*////* $cF STU (STS) immediate -**0- */
    public void stu_im()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	_cpu.CLR_NZV;
    /*TODO*///	_cpu.SET_NZ16(U);
    /*TODO*///    IMM16;
    /*TODO*///	WM16(EAD,&pU);
    }
    /*TODO*///
    /*TODO*////* is this a legal instruction? */
    /*TODO*////* $10cF STS immediate -**0- */
    public void sts_im()
    {
        throw new UnsupportedOperationException("Unsupported");
    /*TODO*///	_cpu.CLR_NZV;
    /*TODO*///	_cpu.SET_NZ16(S);
    /*TODO*///    IMM16;
    /*TODO*///	WM16(EAD,&pS);
    }
    /* $d0 SUBB direct ?**** */
    public void subb_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = _cpu._m6809.b - t &0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    
    }
    /* $d1 CMPB direct ?**** */
    public void cmpb_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = _cpu._m6809.b - t &0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    
    }
    /* $d2 SBCB direct ?**** */
    public void sbcb_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
        r = (_cpu._m6809.b - t - (_cpu._m6809.cc & _cpu.CC_C)) &0xFFFF;//should be unsigned??
        _cpu.CLR_NZVC();
        _cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
        _cpu._m6809.b = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbcb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $d3 ADDD direct -**** */
    public void addd_di()
    {
        int r,d;
        int b=_cpu.DIRWORD();
    	d = getDreg();
    	r = d + b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addd_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $d4 ANDB direct -**0- */
    public void andb_di()
    {
        int t=_cpu.DIRBYTE();
    	_cpu._m6809.b &= t; //TODO should be unsigned?
        _cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d andb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $d5 BITB direct -**0- */
    public void bitb_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = _cpu._m6809.b & t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bitb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $d6 LDB direct -**0- */
    public void ldb_di()
    {
    	_cpu._m6809.b=_cpu.DIRBYTE();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $d7 STB direct -**0- */
    public void stb_di()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
    	_cpu.DIRECT();
    	_cpu.WM(_cpu.ea,_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $d8 EORB direct -**0- */
    public void eorb_di()
    {
        int t=_cpu.DIRBYTE();
    	_cpu._m6809.b ^= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d eorb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $d9 ADCB direct ***** */
    public void adcb_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
        r = (_cpu._m6809.b + t + (_cpu._m6809.cc & _cpu.CC_C)) & 0xFFFF;
        _cpu.CLR_HNZVC();
        _cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
        _cpu.SET_H(_cpu._m6809.b,t,r);
        _cpu._m6809.b= (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adcb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $dA ORB direct -**0- */
    public void orb_di()
    {
        int t=	_cpu.DIRBYTE();
    	_cpu._m6809.b |= t;  //todo check if it should be unsigned
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d orb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $dB ADDB direct ***** */
    public void addb_di()
    {
        int t,r;
        t=_cpu.DIRBYTE();
    	r = _cpu._m6809.b + t &0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu.SET_H(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addb_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $dC LDD direct -**0- */
    public void ldd_di()
    {
      int temp=	_cpu.DIRWORD();
      setDreg(temp);
      _cpu.CLR_NZV();
      _cpu.SET_NZ16(temp);
      if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldd_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $dD STD direct -**0- */
    public void std_di()
    {
    	_cpu.CLR_NZV();
        int temp = getDreg();
    	_cpu.SET_NZ16(temp);
        _cpu.DIRECT();
    	_cpu.WM16(_cpu.ea,temp);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d std_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $dE LDU (LDS) direct -**0- */
    public void ldu_di()
    {
        _cpu._m6809.u=_cpu.DIRWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.u);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldu_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
   
    }
    /* $10dE LDS direct -**0- */
    public void lds_di()
    {
    	_cpu._m6809.s=_cpu.DIRWORD();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.s);
    	_cpu._m6809.int_state |= M6809_LDS;
    }
    /* $dF STU (STS) direct -**0- */
    public void stu_di()
    {
    	_cpu.CLR_NZV();
   	_cpu.SET_NZ16(_cpu._m6809.u);
    	_cpu.DIRECT();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.u);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stu_di :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $10dF STS direct -**0- */
    public void sts_di()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.s);
    	_cpu.DIRECT();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.s);
    }
    /* $e0 SUBB indexed ?**** */
    public void subb_ix()
    {
        int	  t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = _cpu._m6809.b - t & 0xFFFF;
    	_cpu.CLR_NZVC();
   	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $e1 CMPB indexed ?**** */
    public void cmpb_ix()
    {
        /*UINT16*/int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = (_cpu._m6809.b - t) &0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $e2 SBCB indexed ?**** */
    public void sbcb_ix()
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = (_cpu._m6809.b - t - (_cpu._m6809.cc & _cpu.CC_C))&0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbcb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $e3 ADDD indexed -**** */
    public void addd_ix()
    {
    	int r,d;
        int b;
        _cpu.fetch_effective_address();
    	b=_cpu.RM16(_cpu.ea);
    	d = getDreg();
    	r = d + b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addd_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $e4 ANDB indexed -**0- */
    public void andb_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.b &= _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d andb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea); 
    }
    /* $e5 BITB indexed -**0- */
    public void bitb_ix()
    {
      	int r;
    	_cpu.fetch_effective_address();
    	r = _cpu._m6809.b & _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bitb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea); 
    }
    /* $e6 LDB indexed -**0- */
    public void ldb_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.b = _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $e7 STB indexed -**0- */
    public void stb_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
    	_cpu.WM(_cpu.ea,_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        

    }
    /* $e8 EORB indexed -**0- */
    public void eorb_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.b ^= _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d eorb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $e9 ADCB indexed ***** */
    public void adcb_ix()
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = _cpu._m6809.b + t + (_cpu._m6809.cc & _cpu.CC_C) & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu.SET_H(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d adcb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);      
    }
    /* $eA ORB indexed -**0- */
    public void orb_ix()
    {
    	_cpu.fetch_effective_address();
    	_cpu._m6809.b |= _cpu.RM(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d orb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $eB ADDB indexed ***** */
    public void addb_ix()
    {
        int t,r;
    	_cpu.fetch_effective_address();
    	t = _cpu.RM(_cpu.ea);
    	r = _cpu._m6809.b + t & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu.SET_H(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addb_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $eC LDD indexed -**0- */
    public void ldd_ix()
    {
    	_cpu.fetch_effective_address();
        int temp=_cpu.RM16(_cpu.ea);
        setDreg(temp);
    	_cpu.CLR_NZV(); 
        _cpu.SET_NZ16(temp);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldd_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $eD STD indexed -**0- */
    public void std_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
        int temp=getDreg();
    	_cpu.SET_NZ16(temp);
    	_cpu.WM16(_cpu.ea,temp);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d std_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $eE LDU (LDS) indexed -**0- */
    public void ldu_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu._m6809.u=_cpu.RM16(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.u);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldu_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $10eE LDS indexed -**0- */
    public void lds_ix()
    {
        _cpu.fetch_effective_address();
        _cpu._m6809.s=_cpu.RM16(_cpu.ea);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.s);
        _cpu._m6809.int_state |= M6809_LDS;
    }
    /* $eF STU (STS) indexed -**0- */
    public void stu_ix()
    {
    	_cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.u);
    	_cpu.WM16(_cpu.ea,_cpu._m6809.u);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stu_ix :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    
    }
    
    /* $10eF STS indexed -**0- */
    public void sts_ix()
    {
        _cpu.fetch_effective_address();
        _cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.s);
    	_cpu.WM16(_cpu.ea,_cpu._m6809.s);
    }
    /* $f0 SUBB extended ?**** */
    public void subb_ex()
    {
        int  t,r;
        t=_cpu.EXTBYTE();
    	r = _cpu._m6809.b - t & 0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d subb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    
    }
    /* $f1 CMPB extended ?**** */
    public void cmpb_ex()
    {
        int t,r;
    	t=_cpu.EXTBYTE();
    	r = _cpu._m6809.b - t;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d cmpb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
  
    }
    /* $f2 SBCB extended ?**** */
    public void sbcb_ex()
    {
        int t,r;
    	t = _cpu.EXTBYTE();
    	r = (_cpu._m6809.b - t - (_cpu._m6809.cc & _cpu.CC_C))&0xFFFF;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d sbcb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $f3 ADDD extended -**** */
    public void addd_ex()
    {
        int r,d;
        int b=_cpu.EXTWORD();
    	d = getDreg();
    	r = d + b;
    	_cpu.CLR_NZVC();
    	_cpu.SET_FLAGS16(d,b,r);
    	setDreg(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addd_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }
    /* $f4 ANDB extended -**0- */
    public void andb_ex()
    {
        int t = _cpu.EXTBYTE();
        _cpu._m6809.b &=t;
        _cpu.CLR_NZV();
        _cpu.SET_NZ8(_cpu._m6809.b);
    }
    /* $f5 BITB extended -**0- */
    public void bitb_ex()
    {
        int t,r;
        t =_cpu.EXTBYTE();
    	r = _cpu._m6809.b & t;
    	_cpu.CLR_NZV(); 
        _cpu.SET_NZ8(r);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d bitb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
        
    }
    /* $f6 LDB extended -**0- */
    public void ldb_ex()
    {
        _cpu._m6809.b=_cpu.EXTBYTE();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $f7 STB extended -**0- */
    public void stb_ex()
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
    	_cpu.EXTENDED();
    	_cpu.WM(_cpu.ea,_cpu._m6809.b);
        //if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d stb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
    }
    /* $f8 EORB extended -**0- */
    public void eorb_ex()
    {
        int t=_cpu.EXTBYTE();
    	_cpu._m6809.b ^= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
    }
    /* $f9 ADCB extended ***** */
    public void adcb_ex()
    {
        int  t,r;
    	t=_cpu.EXTBYTE();
    	r = (_cpu._m6809.b + t + (_cpu._m6809.cc & _cpu.CC_C)) & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu.SET_H(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);;
    }
    /* $fA ORB extended -**0- */
    public void orb_ex()
    {
        int t=_cpu.EXTBYTE();
    	_cpu._m6809.b |= t;
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ8(_cpu._m6809.b);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d orb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);

    }

    public void addb_ex()//checked
    {
        int  t,r;
    	t=_cpu.EXTBYTE();
    	r = _cpu._m6809.b + t & 0xFFFF;
    	_cpu.CLR_HNZVC();
    	_cpu.SET_FLAGS8(_cpu._m6809.b,t,r);
    	_cpu.SET_H(_cpu._m6809.b,t,r);
    	_cpu._m6809.b = (char)(r& 0xFF);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d addb_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $fC LDD extended -**0- */
    public void ldd_ex()
    {
    	int temp=_cpu.EXTWORD();
        setDreg(temp);
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(temp);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d ldd_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $fD STD extended -**0- */
    public void std_ex()
    {
    	_cpu.CLR_NZV();
        int temp = getDreg();
    	_cpu.SET_NZ16(temp);
        _cpu.EXTENDED();
    	_cpu.WM16(_cpu.ea,temp);
        if(_cpu.m6809log!=null) fprintf(_cpu.m6809log,"M6809#%d std_ex :PC:%d,PPC:%d,A:%d,B:%d,D:%d,DP:%d,U:%d,S:%d,X:%d,Y:%d,CC:%d,EA:%d\n", cpu_getactivecpu(),(int)_cpu._m6809.pc,(int)_cpu._m6809.ppc,(int)_cpu._m6809.a,(int)_cpu._m6809.b,getDreg(),(int)_cpu._m6809.dp,(int)_cpu._m6809.u,(int)_cpu._m6809.s,(int)_cpu._m6809.x,(int)_cpu._m6809.y,(int)_cpu._m6809.cc,_cpu.ea);
 
    }
    /* $fE LDU (LDS) extended -**0- */
    public void ldu_ex()
    {
    	_cpu._m6809.u=_cpu.EXTWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.u);
    }
    /* $10fE LDS extended -**0- */
    public void lds_ex()
    {
        _cpu._m6809.s=_cpu.EXTWORD();
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.s);
	_cpu._m6809.int_state |= M6809_LDS;
    }

    public void stu_ex()//checked
    {
    	_cpu.CLR_NZV();
   	_cpu.SET_NZ16(_cpu._m6809.u);
    	_cpu.EXTENDED();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.u);
    }

    public void sts_ex()//checked
    {
    	_cpu.CLR_NZV();
    	_cpu.SET_NZ16(_cpu._m6809.s);
    	_cpu.EXTENDED();
    	_cpu.WM16(_cpu.ea,_cpu._m6809.s);
    }
    /* $10xx opcodes */
    public void pref10()
    {
    	int ireg2 = _cpu.ROP(_cpu._m6809.pc) &0xFF;
        _cpu._m6809.pc = (char)(_cpu._m6809.pc + 1);
    	switch( ireg2 )
    	{
    		case 0x21: lbrn();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x22: lbhi();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x23: lbls();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x24: lbcc();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x25: lbcs();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x26: lbne();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x27: lbeq();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x28: lbvc();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x29: lbvs();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x2a: lbpl();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x2b: lbmi();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x2c: lbge();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x2d: lblt();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x2e: lbgt();		_cpu.m6809_ICount[0]-=5;	break;
    		case 0x2f: lble();		_cpu.m6809_ICount[0]-=5;	break;

    		case 0x3f: swi2();		_cpu.m6809_ICount[0]-=20;	break;

    		case 0x83: cmpd_im();	_cpu.m6809_ICount[0]-=5;	break;
    		case 0x8c: cmpy_im();	_cpu.m6809_ICount[0]-=5;	break;
    		case 0x8e: ldy_im();	_cpu.m6809_ICount[0]-=4;	break;
    /*TODO*///		case 0x8f: sty_im();	_cpu.m6809_ICount[0]-=4;	break;
    /*TODO*///
    		case 0x93: cmpd_di();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0x9c: cmpy_di();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0x9e: ldy_di();	_cpu.m6809_ICount[0]-=6;	break;
    		case 0x9f: sty_di();	_cpu.m6809_ICount[0]-=6;	break;
    
    		case 0xa3: cmpd_ix();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0xac: cmpy_ix();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0xae: ldy_ix();	_cpu.m6809_ICount[0]-=6;	break;
    		case 0xaf: sty_ix();	_cpu.m6809_ICount[0]-=6;	break;
    
    		case 0xb3: cmpd_ex();	_cpu.m6809_ICount[0]-=8;	break;
    		case 0xbc: cmpy_ex();	_cpu.m6809_ICount[0]-=8;	break;
    		case 0xbe: ldy_ex();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0xbf: sty_ex();	_cpu.m6809_ICount[0]-=7;	break;
    /*TODO*///
    		case 0xce: lds_im();	_cpu.m6809_ICount[0]-=4;	break;
    /*TODO*///		case 0xcf: sts_im();	_cpu.m6809_ICount[0]-=4;	break;
    /*TODO*///
    		case 0xde: lds_di();	_cpu.m6809_ICount[0]-=4;	break;
    		case 0xdf: sts_di();	_cpu.m6809_ICount[0]-=4;	break;
    /*TODO*///
    		case 0xee: lds_ix();	_cpu.m6809_ICount[0]-=6;	break;
    
                case 0xef: sts_ix();	_cpu.m6809_ICount[0]-=6;	break;
    /*TODO*///
    		case 0xfe: lds_ex();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0xff: sts_ex();	_cpu.m6809_ICount[0]-=7;	break;
    /*TODO*///
    /*TODO*///		default:   illegal();						break;
            default:
                System.out.println("6809 prefix10 opcode 0x"+Integer.toHexString(ireg2));
        }
    }
    /* $11xx opcodes */
    public void pref11()
    {
        int ireg2 = _cpu.ROP(_cpu._m6809.pc) &0xFF;
        _cpu._m6809.pc = (char)(_cpu._m6809.pc + 1);
    	switch( ireg2 )
    	{
    /*TODO*///		case 0x3f: swi3();		_cpu.m6809_ICount[0]-=20;	break;

    		case 0x83: cmpu_im();	_cpu.m6809_ICount[0]-=5;	break;
    		case 0x8c: cmps_im();	_cpu.m6809_ICount[0]-=5;	break;

    		case 0x93: cmpu_di();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0x9c: cmps_di();	_cpu.m6809_ICount[0]-=7;	break;

    		case 0xa3: cmpu_ix();	_cpu.m6809_ICount[0]-=7;	break;
    		case 0xac: cmps_ix();	_cpu.m6809_ICount[0]-=7;	break;

    		case 0xb3: cmpu_ex();	_cpu.m6809_ICount[0]-=8;	break;
    		case 0xbc: cmps_ex();	_cpu.m6809_ICount[0]-=8;	break;

    /*TODO*///		default:   illegal();						break;
             default:
                System.out.println("6809 prefix11 opcode 0x"+Integer.toHexString(ireg2));
    	}
    }
    
}
