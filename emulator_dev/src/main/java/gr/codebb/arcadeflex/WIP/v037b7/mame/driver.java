/** ported to v0.37b7 */
package gr.codebb.arcadeflex.WIP.v037b7.mame;

import static gr.codebb.arcadeflex.WIP.v037b7.drivers.astrocde.*;
// WIP drivers
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.leland.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.dday.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.lwings.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.mcr1.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.mcr2.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.scramble.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.senjyo.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.slapfght.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.system1.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.tehkanwc.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.vastar.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.vulgus.*;
import static gr.codebb.arcadeflex.v037b7.drivers.amidar.*;
import static gr.codebb.arcadeflex.v037b7.drivers.cclimber.*;
import static gr.codebb.arcadeflex.v037b7.drivers.frogger.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.kingobox.*;
import static gr.codebb.arcadeflex.v037b7.drivers.kyugo.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.mitchell.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.pacman.*;
import static gr.codebb.arcadeflex.v037b7.drivers.scobra.*;
import static gr.codebb.arcadeflex.v037b7.drivers.snk.*;
import static gr.codebb.arcadeflex.v037b7.drivers.taito_l.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.tsamurai.*;
import static gr.codebb.arcadeflex.v037b7.mame.driverH.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.dooyong.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.gladiatr.*;

//new drivers
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.mnight.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.exerion.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.polepos.*;
import static gr.codebb.arcadeflex.v037b7.drivers.hal21.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.mcr3.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.popeye.*;
import static gr.codebb.arcadeflex.v037b7.drivers.buggychl.*;
import static gr.codebb.arcadeflex.v037b7.drivers.mexico86.*;
import static gr.codebb.arcadeflex.v037b7.drivers.lsasquad.*;
import static gr.codebb.arcadeflex.v037b7.drivers.arkanoid.*;
import static gr.codebb.arcadeflex.v037b7.drivers.taitosj.*;
import static gr.codebb.arcadeflex.v037b7.drivers.spacefb.*;
import static gr.codebb.arcadeflex.v037b7.drivers.mario.*;
import static gr.codebb.arcadeflex.v037b7.drivers.gyruss.*;
import static gr.codebb.arcadeflex.v037b7.drivers.vigilant.*;
import static gr.codebb.arcadeflex.v037b7.drivers.vicdual.*;
import static gr.codebb.arcadeflex.v037b7.drivers.shisen.*;
import static gr.codebb.arcadeflex.v037b7.drivers.wc90.*;
import static gr.codebb.arcadeflex.v037b7.drivers.wc90b.*;
import static gr.codebb.arcadeflex.v037b7.drivers.bublbobl.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.turbo.*;
import static gr.codebb.arcadeflex.v037b7.drivers.lkage.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.dkong.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.psychic5.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.sidearms.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.gberet.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.cosmic.*;
import static gr.codebb.arcadeflex.v037b7.drivers.dotrikun.*;
import static gr.codebb.arcadeflex.v037b7.drivers.sega.*;
import static gr.codebb.arcadeflex.v037b7.drivers.segar.*;
import static gr.codebb.arcadeflex.v037b7.drivers.skydiver.*;
import static gr.codebb.arcadeflex.v037b7.drivers.zaccaria.*;
import static gr.codebb.arcadeflex.v037b7.drivers.seicross.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.m62.*;
import static gr.codebb.arcadeflex.v037b7.drivers.shaolins.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.jackal.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.starwars.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.irobot.*;
import static gr.codebb.arcadeflex.v037b7.drivers.mpatrol.*;
import static gr.codebb.arcadeflex.v037b7.drivers.yard.*;
import static gr.codebb.arcadeflex.v037b7.drivers.travrusa.*;
import static gr.codebb.arcadeflex.v037b7.drivers.troangel.*;
import static gr.codebb.arcadeflex.v037b7.drivers.circusc.*;
import static gr.codebb.arcadeflex.v037b7.drivers.ddrible.*;
import static gr.codebb.arcadeflex.v037b7.drivers.mikie.*;
import static gr.codebb.arcadeflex.v037b7.drivers.sbasketb.*;
import static gr.codebb.arcadeflex.v037b7.drivers.citycon.*;
import static gr.codebb.arcadeflex.v037b7.drivers.finalizr.*;
import static gr.codebb.arcadeflex.v037b7.drivers.ironhors.*;
import static gr.codebb.arcadeflex.v037b7.drivers.mappy.*;
import static gr.codebb.arcadeflex.v037b7.drivers.phozon.*;
import static gr.codebb.arcadeflex.v037b7.drivers.sonson.*;
import static gr.codebb.arcadeflex.v037b7.drivers.superpac.*;
import static gr.codebb.arcadeflex.v037b7.drivers.yiear.*;
import static gr.codebb.arcadeflex.v037b7.drivers.rocnrope.*;
import static gr.codebb.arcadeflex.v037b7.drivers.ddragon.*;
import static gr.codebb.arcadeflex.v037b7.drivers.battlane.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.namcos1.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.pacland.*;
import static gr.codebb.arcadeflex.v037b7.drivers.brkthru.*;
import static gr.codebb.arcadeflex.v037b7.drivers.gaplus.*;
import static gr.codebb.arcadeflex.v037b7.drivers.grobda.*;
import static gr.codebb.arcadeflex.v037b7.drivers.jailbrek.*;
import static gr.codebb.arcadeflex.v037b7.drivers.junofrst.*;
import static gr.codebb.arcadeflex.v037b7.drivers.megazone.*;
import static gr.codebb.arcadeflex.v037b7.drivers.pandoras.*;
import static gr.codebb.arcadeflex.v037b7.drivers.tutankhm.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.hyperspt.*;
import static gr.codebb.arcadeflex.v037b7.drivers.trackfld.*;
import static gr.codebb.arcadeflex.v037b7.drivers.mystston.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.gng.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.gottlieb.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.combatsc.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.jedi.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.capbowl.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.balsente.*;
import static gr.codebb.arcadeflex.v037b7.drivers.asteroid.*;
import static gr.codebb.arcadeflex.v037b7.drivers.atetris.*;
import static gr.codebb.arcadeflex.v037b7.drivers.bogeyman.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.btime.*;
import static gr.codebb.arcadeflex.v037b7.drivers.bwidow.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.bzone.*;
import static gr.codebb.arcadeflex.v037b7.drivers.ccastles.*;
import static gr.codebb.arcadeflex.v037b7.drivers.centiped.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.exprraid.*;
import static gr.codebb.arcadeflex.v037b7.drivers.gameplan.*;
import static gr.codebb.arcadeflex.v037b7.drivers.matmania.*;
import static gr.codebb.arcadeflex.v037b7.drivers.renegade.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.tempest.*;
import static gr.codebb.arcadeflex.v037b7.drivers.mhavoc.*;
import static gr.codebb.arcadeflex.v037b7.drivers.missile.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.exidy.*;
import static gr.codebb.arcadeflex.v037b7.drivers.warlord.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.zac2650.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.lazercmd.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.meadows.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.actfancr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.battlera.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.punchout.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.playch10.*;
import static gr.codebb.arcadeflex.v037b7.drivers.sidepckt.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.dec8.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.pcktgal.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.videopin.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.victory.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.tankbatt.*;
import static gr.codebb.arcadeflex.v037b7.drivers.tagteam.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.subs.*;
import static gr.codebb.arcadeflex.v037b7.drivers.ssozumo.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.sprint2.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.skychut.*;
import static gr.codebb.arcadeflex.v037b7.drivers.scregg.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.shootout.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.sbrkout.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.rockola.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.qwakprot.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.pinbo.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.mole.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.nitedrvr.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.dominos.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.milliped.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.leprechn.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.firetrap.*;
import static gr.codebb.arcadeflex.v037b7.drivers.xain.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.williams.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.lasso.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.cloud9.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.cloak.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.phoenix.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.meteor.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers._8080bw.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.geebee.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.safarir.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.redalert.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.warpwarp.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.starcrus.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.stactics.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.wardner.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.skykid.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.baraduke.*;
import static gr.codebb.arcadeflex.WIP.v037b7.drivers.namcos86.*;

public class driver {

    public static GameDriver drivers[] = {
        /**
         * Playable
         */
        driver_pacman,
        driver_pacmanjp,
        driver_pacmanm,
        driver_npacmod,
        driver_pacmod,
        driver_hangly,
        driver_hangly2,
        driver_puckman,
        driver_pacheart,
        driver_piranha,
        driver_pacplus,
        driver_mspacman,
        driver_mspacatk,
        driver_pacgal,
        driver_maketrax,
        driver_crush,
        driver_crush2,
        driver_crush3,
        driver_mbrush,
        driver_eyes,
        driver_eyes2,
        driver_mrtnt,
        driver_ponpoko,
        driver_ponpokov,
        driver_lizwiz,
        driver_theglob,
        driver_beastf,
        driver_dremshpr,
        driver_vanvan,
        driver_vanvans,
        driver_alibaba,
        driver_sectionz,
        driver_sctionza,
        driver_lwings,
        driver_lwings2,
        driver_lwingsjp,
        driver_kingofb,
        driver_ringking,
        driver_tsamurai, // small articafts right-left of screen
        driver_tsamura2, // small articafts right-left of screen
        driver_nunchaku,
        driver_yamagchi,
        driver_tehkanwc,
        driver_scramble,
        driver_scrambls,
        driver_atlantis,
        driver_atlants2,
        driver_theend,
        driver_theends,
        driver_ckongs,
        driver_froggers,
        driver_amidars,
        driver_triplep,
        driver_knockout,
        driver_mariner,
        driver_800fath,
        driver_mars,
        driver_devilfsh,
        driver_newsin7,
        driver_hotshock,
        driver_mgakuen,
        driver_mgakuen2,
        driver_pkladies,
        driver_dokaben,
        driver_pang,
        driver_pangb,
        driver_bbros,
        driver_pompingw,
        driver_cworld,
        driver_hatena,
        driver_spang,
        driver_sbbros,
        driver_marukin,
        driver_qtono1,
        driver_qsangoku,
        driver_block,
        driver_blockj,
        driver_blockbl,
        driver_solarfox,
        driver_kick,
        driver_kicka,
        driver_shollow,
        driver_shollow2,
        driver_tron,
        driver_tron2,
        driver_kroozr,
        driver_domino,
        driver_wacko,
        driver_twotiger,
        driver_journey,
        /**
         * wrong colors
         */
        driver_dday,
        driver_ddayc,
        /**
         * freezes (unknown reason)
         */
        driver_gridiron,
        /**
         * screen size issues
         */
        driver_tigerhb1,
        driver_tigerhb2,
        driver_slapbtjp,
        driver_slapbtuk,
        driver_getstarb,
        /**
         * gfx issues
         */
        driver_ringkin3, // wrong graphics but might be because of bad dump
        /**
         * tilemap issues
         */
        driver_vulgus,
        driver_vulgus2,
        driver_vulgusj,
        driver_trojan,
        driver_trojanr,
        driver_trojanj,
        /**
         * No sound and tilemap issues
         */
        driver_senjyo,
        driver_starforc,
        driver_starfore,
        driver_megaforc,
        driver_baluba,
        /**
         * GAME NOT_WORKING Flag
         */
        driver_jumpshot,
        driver_shtngmst,
        driver_chplft,
        driver_tigerh,
        driver_tigerh2,
        driver_tigerhj,
        driver_slapfigh,
        driver_alcon,
        driver_getstar,
        driver_getstarj,
        driver_avengers,
        driver_avenger2,
        driver_ringkin2,
        driver_teedoff,
        /**
         * TESTDRIVER
         */
        // TESTDRIVER popflamb,
        /**
         * TODO
         */
        /* Sega System 1 / System 2 games */
        driver_starjack,
        driver_starjacs,
        driver_regulus,
        driver_regulusu,
        driver_upndown,
        driver_swat,
        driver_flicky,
        driver_flicky2,
        driver_bullfgtj,
        driver_pitfall2,
        driver_pitfallu,
        driver_imsorry,
        driver_imsorryj,
        driver_myhero,
        driver_myheroj,
        driver_myherok,
        driver_chplftb,
        driver_chplftbl,
        driver_brain,
        driver_mrviking, /* 834-5383 (c) 1984 (S1) */
        driver_mrvikinj, /* 834-5383 (c) 1984 (S1) */
        driver_seganinj, /* 834-5677 (c) 1985 (S1) */
        driver_seganinu, /* 834-5677 (c) 1985 (S1) */
        driver_nprinces, /* 834-5677 (c) 1985 (S1) */
        driver_nprincsu, /* 834-5677 (c) 1985 (S1) */
        driver_nprincsb, /* bootleg? (S1) */
        driver_teddybb, /* 834-5712 (c) 1985 (S1) */
        driver_hvymetal, /* 834-5745 (c) 1985 (S2?) */
        driver_4dwarrio, /* 834-5918 (c) 1985 Coreland / Sega (S1) */
        /* (c) 1986 Coreland / Sega (S2?) */
        driver_wboy, /* 834-5984 (c) 1986 + Escape license (S1) */
        driver_wboy2, /* 834-5984 (c) 1986 + Escape license (S1) */
        driver_wboy3,
        driver_wboy4, /* 834-5984 (c) 1986 + Escape license (S1) */
        driver_wboyu, /* 834-5753 (? maybe a conversion) (c) 1986 + Escape license (S1) */
        driver_wboy4u, /* 834-5984 (c) 1986 + Escape license (S1) */
        driver_wbdeluxe, /* (c) 1986 + Escape license (S1) */
        driver_gardia, /* 834-6119 (S2?) */
        driver_gardiab, /* bootleg */
        driver_blockgal, /* 834-6303 (S1) */
        driver_blckgalb, /* bootleg */
        driver_tokisens, /* (c) 1987 (from a bootleg board) (S2) */
        driver_wbml, /* bootleg (S2) */
        driver_wbmlj, /* (c) 1987 Sega/Westone (S2) */
        driver_wbmlj2, /* (c) 1987 Sega/Westone (S2) */
        driver_wbmlju, /* bootleg? (S2) */
        driver_dakkochn, /* 836-6483? (S2) */
        driver_ufosensi, /* 834-6659 (S2) */
        /* "Scramble hardware" (and variations) games */
        driver_hunchbks,
        /*TODO*/
        // driver_magmax,	/* (c) 1985 */
        /*TODO*/
        // driver_terracre,	/* (c) 1985 */
        /*TODO*/
        // driver_terracrb,	/* (c) 1985 */
        /*TODO*/
        // driver_terracra,	/* (c) 1985 */

        /*TODO*/
        // driver_terraf,	/* (c) 1987 */
        /*TODO*/
        // driver_terrafu,	/* (c) 1987 Nichibutsu USA */
        /*TODO*/
        // driver_kodure,	/* (c) 1987 (Japan) */
        /*TODO*/
        // driver_armedf,	/* (c) 1988 */
        /*TODO*/
        // driver_cclimbr2,	/* (c) 1988 (Japan) */

        /* "Phoenix hardware" (and variations) games */
        driver_safarir,	/* Shin Nihon Kikaku (SNK) */
        driver_phoenix,	/* (c) 1980 Amstar */
        driver_phoenixa,	/* (c) 1980 Amstar + Centuri license */
        driver_phoenixt,	/* (c) 1980 Taito */
        driver_phoenix3,	/* bootleg */
        driver_phoenixc,	/* bootleg */
        driver_pleiads,	/* (c) 1981 Tehkan */
        driver_pleiadbl,	/* bootleg */
        driver_pleiadce,	/* (c) 1981 Centuri + Tehkan */

        /* Namco games (plus some intruders on similar hardware) */
        driver_geebee,	/* [1978] Namco */
        driver_geebeeg,	/* [1978] Gremlin */
        driver_bombbee,	/* [1979] Namco */
        driver_cutieq,	/* (c) 1979 Namco */
        driver_navalone,	/* (c) 1980 Namco */
        driver_kaitei,	/* [1980] K.K. Tokki */
        driver_kaitein,	/* [1980] Namco */
        driver_sos,		/* [1980] Namco */
        driver_tankbatt, /* (c) 1980 Namco */
        driver_warpwarp,	/* (c) 1981 Namco */
        driver_warpwarr,	/* (c) 1981 Rock-ola - the high score table says "NAMCO" */
        driver_warpwar2,	/* (c) 1981 Rock-ola - the high score table says "NAMCO" */

        /* Libble Rabble board (first Japanese game using a 68000) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( liblrabl,	/* (c) 1983 */
        /*TODO*/
        // driver_toypop,	/* (c) 1986 */
        /* Z8000 games */
        driver_polepos, /* (c) 1982  */
        driver_poleposa, /* (c) 1982 + Atari license */
        driver_polepos1, /* (c) 1982 Atari */
        driver_topracer, /* bootleg */
        driver_polepos2, /* (c) 1983 */
        driver_poleps2a, /* (c) 1983 + Atari license */
        driver_poleps2b, /* bootleg */
        driver_poleps2c, /* bootleg */
        /* no custom I/O in the following, HD63701 (or compatible) microcontroller instead */
        driver_pacland, /* (c) 1984 */
        driver_pacland2, /* (c) 1984 */
        driver_pacland3, /* (c) 1984 */
        driver_paclandm, /* (c) 1984 Midway */
        driver_drgnbstr,	/* (c) 1984 */
        driver_skykid,	/* (c) 1985 */
        driver_baraduke,	/* (c) 1985 */
        driver_metrocrs,	/* (c) 1985 */

        /* Namco System 86 games */
        driver_hopmappy,	/* (c) 1986 */
        driver_skykiddx,	/* (c) 1986 */
        driver_skykiddo,	/* (c) 1986 */
        driver_roishtar,	/* (c) 1986 */
        driver_genpeitd,	/* (c) 1986 */
        driver_rthunder,	/* (c) 1986 new version */
        driver_rthundro,	/* (c) 1986 old version */
        driver_wndrmomo,	/* (c) 1987 */

        /* Namco System 1 games */
        driver_shadowld, /* (c) 1987 */
        /*TODO*/
        // driver_youkaidk,	/* (c) 1987 (Japan new version) */
        /*TODO*/
        // driver_yokaidko,	/* (c) 1987 (Japan old version) */
        /*TODO*/
        // driver_dspirit,	/* (c) 1987 new version */
        /*TODO*/
        // driver_dspirito,	/* (c) 1987 old version */
        /*TODO*/
        // driver_blazer,	/* (c) 1987 (Japan) */
        /*TODO*/
        // driver_quester,	/* (c) 1987 (Japan) */
        driver_pacmania, /* (c) 1987 */
        /*TODO*/
        // driver_pacmanij,	/* (c) 1987 (Japan) */
        driver_galaga88, /* (c) 1987 */
        /*TODO*/
        // driver_galag88b,	/* (c) 1987 */
        /*TODO*/
        // driver_galag88j,	/* (c) 1987 (Japan) */
        /*TODO*/
        // driver_ws,		/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_berabohm,	/* (c) 1988 (Japan) */
        /* 1988 Alice in Wonderland (English version of Marchen maze) */
        /*TODO*/
        // driver_mmaze,		/* (c) 1988 (Japan) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( bakutotu,	/* (c) 1988 */
        /*TODO*/
        // driver_wldcourt,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_splatter,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_faceoff,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_rompers,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_romperso,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_blastoff,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_ws89,		/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_dangseed,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_ws90,		/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_pistoldm,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_soukobdx,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_puzlclub,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_tankfrce,	/* (c) 1991 (US) */
        /*TODO*/
        // driver_tankfrcj,	/* (c) 1991 (Japan) */

        /* Namco System 2 games */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finallap,	/* 87.12 Final Lap */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finalapd,	/* 87.12 Final Lap */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finalapc,	/* 87.12 Final Lap */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finlapjc,	/* 87.12 Final Lap */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finlapjb,	/* 87.12 Final Lap */
        /*TODO*/
        // driver_assault,	/* (c) 1988 */
        /*TODO*/
        // driver_assaultj,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_assaultp,	/* (c) 1988 (Japan) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( metlhawk,	/* (c) 1988 */
        /*TODO*/
        // driver_mirninja,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_ordyne,	/* (c) 1988 */
        /*TODO*/
        // driver_phelios,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_burnforc,	/* (c) 1989 (Japan) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( dirtfoxj,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_finehour,	/* (c) 1989 (Japan) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( fourtrax,	/* 89.11 */
        /*TODO*/
        // driver_marvland,	/* (c) 1989 (US) */
        /*TODO*/
        // driver_marvlanj,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_valkyrie,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_kyukaidk,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_kyukaido,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_dsaber,	/* (c) 1990 */
        /*TODO*/
        // driver_dsaberj,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_rthun2,	/* (c) 1990 */
        /*TODO*/
        // driver_rthun2j,	/* (c) 1990 (Japan) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finalap2,	/* 90.8  Final Lap 2 */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finalp2j,	/* 90.8  Final Lap 2 (Japan) */
        /* 91.3  Steel Gunner */
        /* 91.7  Golly Ghost */
        /* 91.9  Super World Stadium */
        /*TODO*/
        /// *TODO*///TESTDRIVER( sgunner2,	/* (c) 1991 (Japan) */
        /*TODO*/
        // driver_cosmogng,	/* (c) 1991 (US) */
        /*TODO*/
        // driver_cosmognj,	/* (c) 1991 (Japan) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( finalap3,	/* 92.9  Final Lap 3 */
        /*TODO*/
        /// *TODO*///TESTDRIVER( suzuka8h,
        /* 92.8  Bubble Trouble */
        /*TODO*/
        // driver_sws92,		/* (c) 1992 (Japan) */
        /* 93.4  Lucky & Wild */
        /*TODO*/
        /// *TODO*///TESTDRIVER( suzuk8h2,
        /*TODO*/
        // driver_sws93,		/* (c) 1993 (Japan) */
        /* 93.6  Super World Stadium '93 */
        /* Universal games */
        driver_cosmicg, /* 7907 (c) 1979 */
        driver_cosmica, /* 7910 (c) [1979] */
        driver_cosmica2, /* 7910 (c) 1979 */
        driver_panic, /* (c) 1980 */
        driver_panica, /* (c) 1980 */
        driver_panicger, /* (c) 1980 */
        driver_magspot2, /* 8013 (c) [1980] */
        driver_devzone, /* 8022 (c) [1980] */
        driver_nomnlnd, /* (c) [1980?] */
        driver_nomnlndg, /* (c) [1980?] + Gottlieb */
        /* Nintendo games */
        driver_radarscp,
        driver_dkong,
        driver_dkongjp,
        driver_dkongjpo,
        driver_dkongjr,
        driver_dkngjrjp,
        driver_dkjrjp,
        driver_dkjrbl,
        driver_dkong3, /* (c) 1983 Nintendo of America */
        driver_dkong3j, /* (c) 1983 Nintendo */
        driver_hunchbkd, /* (c) 1983 Century */
        driver_herbiedk, /* (c) 1984 CVS */
        /// *TODO*///TESTDRIVER( herocast,
        driver_popeye,
        driver_popeye2,
        driver_popeyef,
        driver_popeyebl, /* bootleg */
        driver_punchout, /* (c) 1984 */
        driver_spnchout, /* (c) 1984 */
        driver_spnchotj, /* (c) 1984 (Japan) */
        driver_armwrest, /* (c) 1985 */
        /* Nintendo Playchoice 10 games */
        driver_pc_tenis, /* (c) 1983 Nintendo */
        driver_pc_bball, /* (c) 1984 Nintendo of America */
        driver_pc_bfght, /* (c) 1984 Nintendo */
        driver_pc_ebike, /* (c) 1984 Nintendo */
        driver_pc_golf, /* (c) 1984 Nintendo */
        driver_pc_kngfu, /* (c) 1984 Irem (Nintendo license) */
        driver_pc_1942, /* (c) 1985 Capcom */
        driver_pc_smb, /* (c) 1985 Nintendo */
        driver_pc_vball, /* (c) 1986 Nintendo */
        driver_pc_duckh, /* (c) 1984 Nintendo */
        driver_pc_hgaly, /* (c) 1984 Nintendo */
        driver_pc_wgnmn, /* (c) 1984 Nintendo */
        driver_pc_grdus, /* (c) 1986 Konami */
        driver_pc_tkfld, /* (c) 1987 Konami (Nintendo of America license) */
        driver_pc_pwrst, /* (c) 1986 Nintendo */
        driver_pc_cvnia, /* (c) 1987 Konami (Nintendo of America license) */
        driver_pc_dbldr, /* (c) 1987 Konami (Nintendo of America license) */
        driver_pc_rnatk, /* (c) 1987 Konami (Nintendo of America license) */
        driver_pc_rygar, /* (c) 1987 Tecmo (Nintendo of America license) */
        driver_pc_cntra, /* (c) 1988 Konami (Nintendo of America license) */
        driver_pc_goons, /* (c) 1986 Konami */
        driver_pc_radrc, /* (c) 1987 Square */
        driver_pc_miket, /* (c) 1987 Nintendo */
        driver_pc_rcpam, /* (c) 1987 Rare */
        driver_pc_ngaid, /* (c) 1989 Tecmo (Nintendo of America license) */
        driver_pc_tmnt, /* (c) 1989 Konami (Nintendo of America license) */
        driver_pc_drmro, /* (c) 1990 Nintendo */
        driver_pc_ynoid, /* (c) 1990 Capcom USA (Nintendo of America license) */
        driver_pc_ddrgn,
        driver_pc_ftqst, /* (c) 1989 Sunsoft (Nintendo of America license) */
        driver_pc_rrngr, /* (c) Capcom USA (Nintendo of America license) */
        driver_pc_gntlt, /* (c) 1985 Atari/Tengen (Nintendo of America license) */
        driver_pc_smb2, /* (c) 1988 Nintendo */
        driver_pc_smb3, /* (c) 1988 Nintendo */
        driver_pc_mman3, /* (c) 1990 Capcom USA (Nintendo of America license) */
        driver_pc_radr2, /* (c) 1990 Square (Nintendo of America license) */
        driver_pc_suprc, /* (c) 1990 Konami (Nintendo of America license) */
        driver_pc_tmnt2, /* (c) 1990 Konami (Nintendo of America license) */
        driver_pc_wcup, /* (c) 1990 Technos (Nintendo license) */
        driver_pc_ngai3, /* (c) 1991 Tecmo (Nintendo of America license) */
        driver_pc_pwbld, /* (c) 1991 Taito (Nintendo of America license) */
        driver_pc_rkats, /* (c) 1991 Atlus (Nintendo of America license) */
        driver_pc_cshwk, /* (c) 1989 Rare (Nintendo of America license) */
        driver_pc_moglf, /* (c) 1991 Nintendo */
        /* Midway 8080 b/w games */
        driver_seawolf,	/* 596 [1976] */
        driver_gunfight,	/* 597 [1975] */
        /* 603 - Top Gun [1976] */
        driver_tornbase,	/* 605 [1976] */
        driver_280zzzap,	/* 610 [1976] */
        driver_maze,		/* 611 [1976] */
        driver_boothill,	/* 612 [1977] */
        driver_checkmat,	/* 615 [1977] */
        driver_desertgu,	/* 618 [1977] */
        driver_dplay,		/* 619 [1977] */
        driver_lagunar,	/* 622 [1977] */
        driver_gmissile,	/* 623 [1977] */
        driver_m4,		/* 626 [1977] */
        driver_clowns,	/* 630 [1978] */
        /* 640 - Space Walk [1978] */
        driver_einnings,	/* 642 [1978] Midway */
        driver_shuffle,	/* 643 [1978] */
        driver_dogpatch,	/* 644 [1977] */
        driver_spcenctr,	/* 645 (c) 1980 Midway */
        driver_phantom2,	/* 652 [1979] */
        driver_bowler,	/* 730 [1978] Midway */
        driver_invaders,	/* 739 [1979] */
        driver_blueshrk,	/* 742 [1978] */
        driver_invad2ct,	/* 851 (c) 1980 Midway */
        driver_invadpt2,	/* 852 [1980] Taito */
        driver_invaddlx,	/* 852 [1980] Midway */
        driver_moonbase,	/* Zeta - Nichibutsu */
        /* 870 - Space Invaders Deluxe cocktail */
        driver_earthinv,
        driver_spaceatt,
        driver_sinvzen,
        driver_superinv,
        driver_sinvemag,
        driver_jspecter,
        driver_invrvnge,
        driver_invrvnga,
        driver_galxwars,
        driver_starw,
        driver_lrescue,	/* LR  (c) 1979 Taito */
        driver_grescue,	/* bootleg? */
        driver_desterth,	/* bootleg */
        driver_cosmicmo,	/* Universal */
        driver_rollingc,	/* Nichibutsu */
        driver_sheriff,	/* (c) Nintendo */
        driver_bandido,	/* (c) Exidy */
        driver_ozmawars,	/* Shin Nihon Kikaku (SNK) */
        driver_solfight,	/* bootleg */
        driver_spaceph,	/* Zilec Games */
        driver_schaser,	/* RT  Taito */
        driver_schasrcv,	/* RT  Taito */
        driver_lupin3,	/* LP  (c) 1980 Taito */
        driver_helifire,	/* (c) Nintendo */
        driver_helifira,	/* (c) Nintendo */
        driver_spacefev,
        driver_sfeverbw,
        driver_spclaser,
        driver_laser,
        driver_spcewarl,
        driver_polaris,	/* PS  (c) 1980 Taito */
        driver_polarisa,	/* PS  (c) 1980 Taito */
        driver_ballbomb,	/* TN  (c) 1980 Taito */
        /*TODO*/
        // driver_m79amb,
        driver_alieninv,
        driver_sitv,
        driver_sicv,
        driver_sisv,
        driver_sisv2,
        driver_spacewr3,
        driver_invaderl,
        driver_yosakdon,
        driver_spceking,
        driver_spcewars,

        /* "Midway" Z80 b/w games */
        /*TODO*/
        // driver_astinvad,	/* (c) 1980 Stern */
        /*TODO*/
        // driver_kamikaze,	/* Leijac Corporation */
        /*TODO*/
        // driver_spaceint,	/* [1980] Shoei */

        /* Meadows S2650 games */
        driver_lazercmd, /* [1976?] */
        driver_deadeye, /* [1978?] */
        driver_gypsyjug, /* [1978?] */
        driver_medlanes, /* [1977?] */
        /* Midway "Astrocade" games */
        driver_spacezap, /* (c) 1980 */
        driver_gorf, /* (c) 1981 */
        driver_gorfpgm1, /* (c) 1981 */
        /*TODO*/
        /// *TODO*///TESTDRIVER( profpac,	/* (c) 1983 Bally Midway */

        /* MCR3 */
        driver_tapper,
        driver_tappera,
        driver_sutapper,
        driver_rbtapper,
        driver_timber, /* (c) 1984 */
        driver_dotron, /* (c) 1983 */
        driver_dotrone, /* (c) 1983 */
        driver_destderb,
        driver_destderm,
        driver_sarge,
        driver_rampage, /* (c) 1986 */
        driver_rampage2, /* (c) 1986 */
        driver_powerdrv, /* (c) 1986 */
        driver_maxrpm,
        driver_spyhunt, /* (c) 1983 */
        driver_turbotag, /* (c) 1985 */
        driver_crater,
        /* MCR 68000 */
        /*TODO*/
        // driver_zwackery,	/* (c) 1984 */
        /*TODO*/
        // driver_xenophob,	/* (c) 1987 */
        /*TODO*/
        // driver_spyhunt2,	/* (c) 1987 */
        /*TODO*/
        // driver_spyhnt2a,	/* (c) 1987 */
        /*TODO*/
        // driver_blasted,	/* (c) 1988 */
        /*TODO*/
        // driver_archrivl,	/* (c) 1989 */
        /*TODO*/
        // driver_archriv2,	/* (c) 1989 */
        /*TODO*/
        // driver_trisport,	/* (c) 1989 */
        /*TODO*/
        // driver_pigskin,	/* (c) 1990 */
        /* other possible MCR games:
    Black Belt
    Shoot the Bull
    Special Force
    MotorDome
    Six Flags (?)
         */
        /* Bally / Sente games */
        driver_sentetst,
        driver_cshift, /* (c) 1984 */
        driver_gghost, /* (c) 1984 */
        driver_hattrick, /* (c) 1984 */
        driver_otwalls, /* (c) 1984 */
        driver_snakepit, /* (c) 1984 */
        driver_snakjack, /* (c) 1984 */
        driver_stocker, /* (c) 1984 */
        driver_triviag1, /* (c) 1984 */
        driver_triviag2, /* (c) 1984 */
        driver_triviasp, /* (c) 1984 */
        driver_triviayp, /* (c) 1984 */
        driver_triviabb, /* (c) 1984 */
        driver_gimeabrk, /* (c) 1985 */
        driver_minigolf, /* (c) 1985 */
        driver_minigol2, /* (c) 1985 */
        driver_toggle, /* (c) 1985 */
        driver_nametune, /* (c) 1986 */
        driver_nstocker, /* (c) 1986 */
        driver_sfootbal, /* (c) 1986 */
        driver_spiker, /* (c) 1986 */
        driver_rescraid, /* (c) 1987 */
        driver_rescrdsa, /* (c) 1987 */
        /* Irem games */
        /* trivia: IREM means "International Rental Electronics Machines" */
        /*TODO*/
        /// *TODO*///TESTDRIVER( iremm10,	/* M10 */
        driver_skychut, /* (c) [1980] */
        driver_spacbeam, /* M15 no copyright notice */
        /*TODO*/
        /// *TODO*///TESTDRIVER( greenber,

        driver_redalert,	/* (c) 1981 + "GDI presents" */

        /* M62 */
        driver_kungfum, /* (c) 1984 */
        driver_kungfud, /* (c) 1984 + Data East license */
        driver_spartanx, /* (c) 1984 */
        driver_kungfub, /* bootleg */
        driver_kungfub2, /* bootleg */
        driver_battroad, /* (c) 1984 */
        driver_ldrun, /* (c) 1984 licensed from Broderbund */
        driver_ldruna, /* (c) 1984 licensed from Broderbund */
        driver_ldrun2, /* (c) 1984 licensed from Broderbund */
        driver_ldrun3, /* (c) 1985 licensed from Broderbund */
        driver_ldrun4, /* (c) 1986 licensed from Broderbund */
        driver_lotlot, /* (c) 1985 licensed from Tokuma Shoten */
        driver_kidniki, /* (c) 1986 + Data East USA license */
        driver_yanchamr, /* (c) 1986 (Japan) */
        driver_spelunkr, /* (c) 1985 licensed from Broderbund */
        driver_spelunk2, /* (c) 1986 licensed from Broderbund */
        /* M72 (and derivatives) */
        /*TODO*/
        // driver_rtype,		/* (c) 1987 (Japan) */
        /*TODO*/
        // driver_rtypepj,	/* (c) 1987 (Japan) */
        /*TODO*/
        // driver_rtypeu,	/* (c) 1987 + Nintendo USA license (US) */
        /*TODO*/
        // driver_bchopper,	/* (c) 1987 */
        /*TODO*/
        // driver_mrheli,	/* (c) 1987 (Japan) */
        /*TODO*/
        // driver_nspirit,	/* (c) 1988 */
        /*TODO*/
        // driver_nspiritj,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_imgfight,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_loht,		/* (c) 1989 */
        /*TODO*/
        // driver_xmultipl,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_dbreed,	/* (c) 1989 */
        /*TODO*/
        // driver_rtype2,	/* (c) 1989 */
        /*TODO*/
        // driver_rtype2j,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_majtitle,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_hharry,	/* (c) 1990 (World) */
        /*TODO*/
        // driver_hharryu,	/* (c) 1990 Irem America (US) */
        /*TODO*/
        // driver_dkgensan,	/* (c) 1990 (Japan) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( kengo,
        /*TODO*/
        // driver_poundfor,	/* (c) 1990 (World) */
        /*TODO*/
        // driver_poundfou,	/* (c) 1990 Irem America (US) */
        /*TODO*/
        // driver_airduel,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_gallop,	/* (c) 1991 (Japan) */
        /* not M72, but same sound hardware */
        /* M92 */
        /*TODO*/
        // driver_bmaster,	/* (c) 1991 Irem */
        /*TODO*/
        // driver_gunforce,	/* (c) 1991 Irem (World) */
        /*TODO*/
        // driver_gunforcu,	/* (c) 1991 Irem America (US) */
        /*TODO*/
        // driver_hook,		/* (c) 1992 Irem (World) */
        /*TODO*/
        // driver_hooku,		/* (c) 1992 Irem America (US) */
        /*TODO*/
        // driver_mysticri,	/* (c) 1992 Irem (World) */
        /*TODO*/
        // driver_gunhohki,	/* (c) 1992 Irem (Japan) */
        /*TODO*/
        // driver_uccops,	/* (c) 1992 Irem (World) */
        /*TODO*/
        // driver_uccopsj,	/* (c) 1992 Irem (Japan) */
        /*TODO*/
        // driver_rtypeleo,	/* (c) 1992 Irem (Japan) */
        /*TODO*/
        // driver_majtitl2,	/* (c) 1992 Irem (World) */
        /*TODO*/
        // driver_skingame,	/* (c) 1992 Irem America (US) */
        /*TODO*/
        // driver_skingam2,	/* (c) 1992 Irem America (US) */
        /*TODO*/
        // driver_inthunt,	/* (c) 1993 Irem (World) */
        /*TODO*/
        // driver_inthuntu,	/* (c) 1993 Irem (US) */
        /*TODO*/
        // driver_kaiteids,	/* (c) 1993 Irem (Japan) */
        /*TODO*/
        // TESTDRIVER( nbbatman,	/* (c) 1993 Irem America (US) */
        /*TODO*/
        // TESTDRIVER( leaguemn,	/* (c) 1993 Irem (Japan) */
        /*TODO*/
        // driver_lethalth,	/* (c) 1991 Irem (World) */
        /*TODO*/
        // driver_thndblst,	/* (c) 1991 Irem (Japan) */
        /*TODO*/
        // driver_psoldier,	/* (c) 1993 Irem (Japan) */
        /* M97 */
        /*TODO*/
        // TESTDRIVER( hasamu,	/* M90, probably doesn't belong here */
        /*TODO*/
        // TESTDRIVER( riskchal,
        /*TODO*/
        // TESTDRIVER( gussun,
        /*TODO*/
        // TESTDRIVER( shisen2,
        /*TODO*/
        // TESTDRIVER( quizf1,
        /*TODO*/
        // TESTDRIVER( atompunk,
        /*TODO*/
        // TESTDRIVER( bbmanw,
        /* M107 */
        /*TODO*/
        // TESTDRIVER( firebarr,	/* (c) 1993 Irem (Japan) */
        /*TODO*/
        // driver_dsoccr94,	/* (c) 1994 Irem (Data East Corporation license) */

        /* Gottlieb/Mylstar games (Gottlieb became Mylstar in 1983) */
        driver_reactor, /* GV-100 (c) 1982 Gottlieb */
        driver_mplanets, /* GV-102 (c) 1983 Gottlieb */
        driver_qbert, /* GV-103 (c) 1982 Gottlieb */
        driver_qbertjp, /* GV-103 (c) 1982 Gottlieb + Konami license */
        driver_insector, /* GV-??? (c) 1982 Gottlieb - never released */
        driver_krull, /* GV-105 (c) 1983 Gottlieb */
        driver_sqbert, /* GV-??? (c) 1983 Mylstar - never released */
        driver_mach3, /* GV-109 (c) 1983 Mylstar */
        driver_usvsthem, /* GV-??? (c) 198? Mylstar */
        driver_3stooges, /* GV-113 (c) 1984 Mylstar */
        driver_qbertqub, /* GV-119 (c) 1983 Mylstar */
        driver_screwloo, /* GV-123 (c) 1983 Mylstar - never released */
        driver_curvebal, /* GV-134 (c) 1984 Mylstar */
        /* Taito "Qix hardware" games */
        /*TODO*/
        // driver_qix,		/* LK  (c) 1981 Taito America Corporation */
        /*TODO*/
        // driver_qixa,		/* LK  (c) 1981 Taito America Corporation */
        /*TODO*/
        // driver_qixb,		/* LK  (c) 1981 Taito America Corporation */
        /*TODO*/
        // driver_qix2,		/* ??  (c) 1981 Taito America Corporation */
        /*TODO*/
        // driver_sdungeon,	/* SD  (c) 1981 Taito America Corporation */
        /*TODO*/
        // driver_elecyoyo,	/* YY  (c) 1982 Taito America Corporation */
        /*TODO*/
        // driver_elecyoy2,	/* YY  (c) 1982 Taito America Corporation */
        /*TODO*/
        // driver_kram,		/* KS  (c) 1982 Taito America Corporation */
        /*TODO*/
        // driver_kram2,		/* KS  (c) 1982 Taito America Corporation */
        /*TODO*/
        // driver_zookeep,	/* ZA  (c) 1982 Taito America Corporation */
        /*TODO*/
        // driver_zookeep2,	/* ZA  (c) 1982 Taito America Corporation */
        /*TODO*/
        // driver_zookeep3,	/* ZA  (c) 1982 Taito America Corporation */

        /* other Taito games */
        // TESTDRIVER( flstory,	/* A45 (c) 1985 Taito Corporation */
        /*TODO*/
        // TESTDRIVER( flstoryj,	/* A45 (c) 1985 Taito Corporation (Japan) */
        /*TODO*/
        // TESTDRIVER( onna34ro,	/* A52 */
        driver_gladiatr, /* ??? (c) 1986 Taito America Corporation (US) */
        driver_ogonsiro, /* ??? (c) 1986 Taito Corporation (Japan) */
        // driver_rastan,	/* B04 (c) 1987 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_rastanu,	/* B04 (c) 1987 Taito America Corporation (US) */
        /*TODO*/
        // driver_rastanu2,	/* B04 (c) 1987 Taito America Corporation (US) */
        /*TODO*/
        // driver_rastsaga,	/* B04 (c) 1987 Taito Corporation (Japan)*/
        /*TODO*/
        // driver_rainbow,	/* B22 (c) 1987 Taito Corporation */
        /*TODO*/
        // driver_rainbowe,	/* ??? (c) 1988 Taito Corporation */
        /*TODO*/
        // driver_jumping,	/* bootleg */

        /*TODO*/
        // driver_superman,	/* B61 (c) 1988 Taito Corporation */

        /* Taito B-System games */
        /*TODO*/
        // driver_masterw,	/* B72 (c) 1989 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_nastar,	/* B81 (c) 1988 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_nastarw,	/* B81 (c) 1988 Taito America Corporation (US) */
        /*TODO*/
        // driver_rastsag2,	/* B81 (c) 1988 Taito Corporation (Japan) */
        /*TODO*/
        // driver_rambo3,	/* B93 (c) 1989 Taito Europe Corporation (Europe) */
        /*TODO*/
        // driver_rambo3a,	/* B93 (c) 1989 Taito America Corporation (US) */
        /*TODO*/
        // driver_crimec,	/* B99 (c) 1989 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_crimecu,	/* B99 (c) 1989 Taito America Corporation (US) */
        /*TODO*/
        // driver_crimecj,	/* B99 (c) 1989 Taito Corporation (Japan) */
        /*TODO*/
        // driver_tetrist,	/* C12 (c) 1989 Sega Enterprises,Ltd. (Japan) */
        /*TODO*/
        // driver_viofight,	/* C16 (c) 1989 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_ashura,	/* C43 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_ashurau,	/* C43 (c) 1990 Taito America Corporation (US) */
        /*TODO*/
        // driver_hitice,	/* C59 (c) 1990 Williams (US) */
        /*TODO*/
        // driver_puzbobb,	/* ??? (c) 1994 Taito Corporation (Japan) */
        /*TODO*/
        // driver_qzshowby,	/* D72 (c) 1993 Taito Corporation (Japan) */
        /*TODO*/
        // driver_spacedx,	/* D89 (c) 1994 Taito Corporation (Japan) */
        /*TODO*/
        // driver_silentd,	/* ??? (c) 1992 Taito Corporation Japan (World) */

        /* Taito F2 games */
        /*TODO*/
        // driver_finalb,	/* B82 (c) 1988 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_finalbj,	/* B82 (c) 1988 Taito Corporation (Japan) */
        /*TODO*/
        // driver_dondokod,	/* B95 (c) 1989 Taito Corporation (Japan) */
        /*TODO*/
        // driver_megab,		/* C11 (c) 1989 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_megabj,	/* C11 (c) 1989 Taito Corporation (Japan) */
        /*TODO*/
        // driver_thundfox,	/* C28 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_cameltry,	/* C38 (c) 1989 Taito Corporation (Japan) */
        /*TODO*/
        // driver_cameltru,	/* C38 (c) 1989 Taito America Corporation (US) */
        /*TODO*/
        // driver_qtorimon,	/* C41 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_liquidk,	/* C49 (c) 1990 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_liquidku,	/* C49 (c) 1990 Taito America Corporation (US) */
        /*TODO*/
        // driver_mizubaku,	/* C49 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_quizhq,	/* C53 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_ssi,		/* C64 (c) 1990 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_majest12,	/* C64 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_gunfront,	/* C71 (c) 1990 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_gunfronj,	/* C71 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_growl,		/* C74 (c) 1990 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_growlu,	/* C74 (c) 1990 Taito America Corporation (US) */
        /*TODO*/
        // driver_runark,	/* C74 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_mjnquest,	/* C77 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_mjnquesb,	/* C77 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_footchmp,	/* C80 (c) 1990 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_hthero,	/* C80 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_euroch92,	/*     (c) 1992 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_koshien,	/* C81 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_yuyugogo,	/* C83 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_ninjak,	/* C85 (c) 1990 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_ninjakj,	/* C85 (c) 1990 Taito Corporation (Japan) */
        /*TODO*/
        // driver_solfigtr,	/* C91 (c) 1991 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_qzquest,	/* C92 (c) 1991 Taito Corporation (Japan) */
        /*TODO*/
        // driver_pulirula,	/* C98 (c) 1991 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_pulirulj,	/* C98 (c) 1991 Taito Corporation (Japan) */
        /*TODO*/
        // driver_metalb,	/* D16? (c) 1991 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_metalbj,	/* D12 (c) 1991 Taito Corporation (Japan) */
        /*TODO*/
        // driver_qzchikyu,	/* D19 (c) 1991 Taito Corporation (Japan) */
        /*TODO*/
        // driver_yesnoj,	/* D20 (c) 1992 Taito Corporation (Japan) */
        /*TODO*/
        // driver_deadconx,	/* D28 (c) 1992 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_deadconj,	/* D28 (c) 1992 Taito Corporation (Japan) */
        /*TODO*/
        // driver_dinorex,	/* D39 (c) 1992 Taito Corporation Japan (World) */
        /*TODO*/
        // driver_dinorexj,	/* D39 (c) 1992 Taito Corporation (Japan) */
        /*TODO*/
        // driver_dinorexu,	/* D39 (c) 1992 Taito America Corporation (US) */
        /*TODO*/
        // driver_qjinsei,	/* D48 (c) 1992 Taito Corporation (Japan) */
        /*TODO*/
        // driver_qcrayon,	/* D55 (c) 1993 Taito Corporation (Japan) */
        /*TODO*/
        // driver_qcrayon2,	/* D63 (c) 1993 Taito Corporation (Japan) */
        /*TODO*/
        // driver_driftout,	/* (c) 1991 Visco */
        /*TODO*/
        // driver_driveout,	/* bootleg */

        /* Toaplan games */
        /*TODO*/
        // driver_fshark,	/* TP-007 (c) 1987 Taito Corporation (World) */
        /*TODO*/
        // driver_skyshark,	/* TP-007 (c) 1987 Taito America Corporation + Romstar license (US) */
        /*TODO*/
        // driver_hishouza,	/* TP-007 (c) 1987 Taito Corporation (Japan) */
        /*TODO*/
        // driver_fsharkbt,	/* bootleg */
        driver_wardner,	/* TP-009 (c) 1987 Taito Corporation Japan (World) */
        driver_pyros,		/* TP-009 (c) 1987 Taito America Corporation (US) */
        driver_wardnerj,	/* TP-009 (c) 1987 Taito Corporation (Japan) */
        /*TODO*/
        // driver_twincobr,	/* TP-011 (c) 1987 Taito Corporation (World) */
        /*TODO*/
        // driver_twincobu,	/* TP-011 (c) 1987 Taito America Corporation + Romstar license (US) */
        /*TODO*/
        // driver_ktiger,	/* TP-011 (c) 1987 Taito Corporation (Japan) */

        /*TODO*/
        // driver_rallybik,	/* TP-012 (c) 1988 Taito */
        /*TODO*/
        // driver_truxton,	/* TP-013B (c) 1988 Taito */
        /*TODO*/
        // driver_hellfire,	/* TP-??? (c) 1989 Toaplan + Taito license */
        /*TODO*/
        // driver_zerowing,	/* TP-015 (c) 1989 Toaplan */
        /*TODO*/
        // driver_demonwld,	/* TP-016 (c) 1989 Toaplan + Taito license */
        /*TODO*/
        // driver_fireshrk,	/* TP-017 (c) 1990 Toaplan */
        /*TODO*/
        // driver_samesame,	/* TP-017 (c) 1989 Toaplan */
        /*TODO*/
        // driver_outzone,	/* TP-018 (c) 1990 Toaplan */
        /*TODO*/
        // driver_outzonep,	/* bootleg */
        /*TODO*/
        // driver_vimana,	/* TP-019 (c) 1991 Toaplan (+ Tecmo license when set to Japan) */
        /*TODO*/
        // driver_vimana2,	/* TP-019 (c) 1991 Toaplan (+ Tecmo license when set to Japan)  */
        /*TODO*/
        // driver_vimanan,	/* TP-019 (c) 1991 Toaplan (+ Nova Apparate GMBH & Co license) */
        /*TODO*/
        // driver_snowbros,	/* MIN16-02 (c) 1990 Toaplan + Romstar license */
        /*TODO*/
        // driver_snowbroa,	/* MIN16-02 (c) 1990 Toaplan + Romstar license */
        /*TODO*/
        // driver_snowbrob,	/* MIN16-02 (c) 1990 Toaplan + Romstar license */
        /*TODO*/
        // driver_snowbroj,	/* MIN16-02 (c) 1990 Toaplan */
        /*TODO*/
        // driver_wintbob,	/* bootleg */

        /*TODO*/
        // driver_tekipaki,	/* TP-020 (c) 1991 Toaplan */
        /*TODO*/
        // driver_ghox,		/* TP-021 (c) 1991 Toaplan */
        /*TODO*/
        // driver_dogyuun,	/* TP-022 (c) 1992 Toaplan */
        /*TODO*/
        // driver_kbash,		/* TP-023 (c) 1993 Toaplan */
        /*TODO*/
        // driver_tatsujn2,	/* TP-024 (c) 1992 Toaplan */
        /*TODO*/
        // driver_pipibibs,	/* TP-025 */
        /*TODO*/
        // driver_pipibibi,	/* (c) 1991 Ryouta Kikaku (bootleg?) */
        /*TODO*/
        // driver_whoopee,	/* TP-025 */
        /*TODO*/
        // TESTDRIVER( fixeight,	/* TP-026 (c) 1992 + Taito license */
        /*TODO*/
        // driver_vfive,		/* TP-027 (c) 1993 Toaplan (Japan) */
        /*TODO*/
        // driver_grindstm,	/* TP-027 (c) 1993 Toaplan + Unite Trading license (Korea) */
        /*TODO*/
        // driver_batsugun,	/* TP-030 (c) 1993 Toaplan */
        /*TODO*/
        // driver_batugnsp,	/* TP-??? (c) 1993 Toaplan */
        /*TODO*/
        // driver_snowbro2,	/* TP-??? (c) 1994 Hanafram */
        /*TODO*/
        // driver_mahoudai,	/* (c) 1993 Raizing + Able license */
        /*TODO*/
        // driver_shippumd,	/* (c) 1994 Raizing */

        /*
    Toa Plan's board list
    (translated from http://www.aianet.ne.jp/~eisetu/rom/rom_toha.html)

    Title              ROMno.   Remark(1)   Remark(2)
    --------------------------------------------------
    Tiger Heli           A47      GX-551
    Hishouzame           B02      TP-007
    Kyukyoku Tiger       B30      TP-011
    Dash Yarou           B45      TP-012
    Tatsujin             B65      TP-013B   M6100649A
    Zero Wing            O15      TP-015
    Horror Story         O16      TP-016
    Same!Same!Same!      O17      TP-017
    Out Zone                      TP-018
    Vimana                        TP-019
    Teki Paki            O20      TP-020
    Ghox               TP-21      TP-021
    Dogyuun                       TP-022
    Tatsujin Oh                   TP-024    *1
    Fixeight                      TP-026
    V-V                           TP-027

    *1 There is a doubt this game uses TP-024 board and TP-025 romsets.

       86 Mahjong Sisters                                 Kit 2P 8W+2B     HC    Mahjong TP-
       88 Dash                                            Kit 2P 8W+2B                   TP-
       89 Fire Shark                                      Kit 2P 8W+2B     VC    Shooter TP-017
       89 Twin Hawk                                       Kit 2P 8W+2B     VC    Shooter TP-
       91 Whoopie                                         Kit 2P 8W+2B     HC    Action
       92 Teki Paki                                       Kit 2P                         TP-020
       92 Ghox                                            Kit 2P Paddle+1B VC    Action  TP-021
    10/92 Dogyuun                                         Kit 2P 8W+2B     VC    Shooter TP-022
    92/93 Knuckle Bash                 Atari Games        Kit 2P 8W+2B     HC    Action  TP-023
    10/92 Tatsujin II/Truxton II       Taito              Kit 2P 8W+2B     VC    Shooter TP-024
    10/92 Truxton II/Tatsujin II       Taito              Kit 2P 8W+2B     VC    Shooter TP-024
          Pipi & Bipi                                                                    TP-025
       92 Fix Eight                                       Kit 2P 8W+2B     VC    Action  TP-026
    12/92 V  -  V (5)/Grind Stormer                       Kit 2P 8W+2B     VC    Shooter TP-027
     1/93 Grind Stormer/V - V (Five)                      Kit 2P 8W+2B     VC    Shooter TP-027
     2/94 Batsugun                                        Kit 2P 8W+2B     VC            TP-
     4/94 Snow Bros. 2                                    Kit 2P 8W+2B     HC    Action  TP-
         */
        /* Cave games */
        /* Cave was formed in 1994 from the ruins of Toaplan, like Raizing was. */
        /* Donpachi (c) 1995 */
        /*TODO*/
        // driver_ddonpach,	/* (c) 1997 Atlus/Cave */
        /*TODO*/
        // driver_dfeveron,	/* (c) 1998 Cave + Nihon System license */
        /*TODO*/
        // driver_esprade,	/* (c) 1998 Atlus/Cave */
        /*TODO*/
        // driver_uopoko,	/* (c) 1998 Cave + Jaleco license */
        /*TODO*/
        // TESTDRIVER( guwange,	/* (c) 1999 Atlus/Cave */

        /* Williams games */
        driver_defender,	/* (c) 1980 */
        driver_defendg,	/* (c) 1980 */
        driver_defendw,	/* (c) 1980 */
        //TESTDRIVER( defndjeu,	/* bootleg */
        driver_defcmnd,	/* bootleg */
        driver_defence,	/* bootleg */
        driver_mayday,
        driver_maydaya,
        driver_colony7,	/* (c) 1981 Taito */
        driver_colony7a,	/* (c) 1981 Taito */
        driver_stargate,	/* (c) 1981 */
        driver_robotron,	/* (c) 1982 */        
        driver_robotryo,	/* (c) 1982 */
        driver_joust,		/* (c) 1982 */
        driver_joustr,	/* (c) 1982 */
        driver_joustwr,	/* (c) 1982 */
        driver_bubbles,	/* (c) 1982 */
        driver_bubblesr,	/* (c) 1982 */       
        driver_splat,		/* (c) 1982 */        
        driver_sinistar,	/* (c) 1982 */      
        driver_sinista1,	/* (c) 1982 */       
        driver_sinista2,	/* (c) 1982 */      
        driver_blaster,	/* (c) 1983 */
        driver_mysticm,	/* (c) 1983 */
        driver_tshoot,	/* (c) 1984 */       
        driver_inferno,	/* (c) 1984 */       
        driver_joust2,	/* (c) 1986 */       
        driver_lottofun,	/* (c) 1987 H.A.R. Management */

        /* Capcom games */
        /* The following is a COMPLETE list of the Capcom games up to 1997, as shown on */
        /* their web site. The list is sorted by production date.                       */
        /* A comprehensive list of Capcom games with board info can be found here:      */
        /* http://www.emugaming.com/strider/capcom_list.shtml                           */
        /*TODO*/
        /*TODO*/
        driver_gng, /*  9/1985 (c) 1985 */
        driver_gnga, /*  9/1985 (c) 1985 */
        driver_gngt, /*  9/1985 (c) 1985 */
        driver_makaimur, /*  9/1985 (c) 1985 */
        driver_makaimuc, /*  9/1985 (c) 1985 */
        driver_makaimug, /*  9/1985 (c) 1985 */
        driver_diamond, /* (c) 1989 KH Video (NOT A CAPCOM GAME but runs on GnG hardware) */
        /*TODO*/
        // driver_srumbler,	/*  9/1986 (c) 1986 */
        /*TODO*/
        // driver_srumblr2,	/*  9/1986 (c) 1986 */
        /*TODO*/
        // driver_rushcrsh,	/*  9/1986 (c) 1986 */
        driver_sidearms,
        driver_sidearmr,
        driver_sidearjp,
        driver_turtship,
        driver_dyger,
        driver_dygera,
        /*TODO*/
        // driver_bionicc,	/*  3/1987 (c) 1987 (US) */
        /*TODO*/
        // driver_bionicc2,	/*  3/1987 (c) 1987 (US) */
        /*TODO*/
        // driver_topsecrt,	/*  3/1987 (c) 1987 (Japan) */
        /*TODO*/
        // driver_sf1,		/*  8/1987 (c) 1987 (World) */
        /*TODO*/
        // driver_sf1us,		/*  8/1987 (c) 1987 (US) */
        /*TODO*/
        // driver_sf1jp,		/*  8/1987 (c) 1987 (Japan) */
        /*TODO*/
        // driver_tigeroad,	/* 11/1987 (c) 1987 + Romstar (US) */
        /*TODO*/
        // driver_toramich,	/* 11/1987 (c) 1987 (Japan) */
        /*TODO*/
        // driver_f1dream,	/*  4/1988 (c) 1988 + Romstar */
        /*TODO*/
        // driver_f1dreamb,	/* bootleg */

        /*TODO*/
        // driver_lastduel,	/*  7/1988 (c) 1988 (US) */
        /*TODO*/
        // driver_lstduela,	/*  7/1988 (c) 1988 (US) */
        /*TODO*/
        // driver_lstduelb,	/* bootleg */
        /*TODO*/
        // driver_madgear,	/*  2/1989 (c) 1989 (US) */
        /*TODO*/
        // driver_madgearj,	/*  2/1989 (c) 1989 (Japan) */
        /*TODO*/
        // driver_ledstorm,	/*  2/1989 (c) 1989 (US) */
        /*  3/1989 Dokaben (baseball) - see below among "Mitchell" games */
        /*  8/1989 Dokaben 2 (baseball) - see below among "Mitchell" games */
        /* 10/1989 Capcom Baseball - see below among "Mitchell" games */
        /* 11/1989 Capcom World - see below among "Mitchell" games */
        /*  3/1990 Adventure Quiz 2 Hatena no Dai-Bouken - see below among "Mitchell" games */
        /*  1/1991 Quiz Tonosama no Yabou - see below among "Mitchell" games */
        /*  4/1991 Ashita Tenki ni Naare (golf) - see below among "Mitchell" games */
        /*  5/1991 Ataxx - see below among "Leland" games */
        /*  6/1991 Quiz Sangokushi - see below among "Mitchell" games */
        /* 10/1991 Block Block - see below among "Mitchell" games */
        /*  6/1995 Street Fighter - the Movie - see below among "Incredible Technologies" games */
        /* Capcom CPS1 games */
        /*TODO*/
        // driver_forgottn,	/*  7/1988 (c) 1988 (US) */
        /*TODO*/
        // driver_lostwrld,	/*  7/1988 (c) 1988 (Japan) */
        /*TODO*/
        // driver_ghouls,	/* 12/1988 (c) 1988 (World) */
        /*TODO*/
        // driver_ghoulsu,	/* 12/1988 (c) 1988 (US) */
        /*TODO*/
        // driver_ghoulsj,	/* 12/1988 (c) 1988 (Japan) */
        /*TODO*/
        // driver_strider,	/*  3/1989 (c) 1989 (not explicitly stated but should be US) */
        /*TODO*/
        // driver_striderj,	/*  3/1989 (c) 1989 */
        /*TODO*/
        // driver_stridrja,	/*  3/1989 (c) 1989 */
        /*TODO*/
        // driver_dwj,		/*  4/1989 (c) 1989 (Japan) */
        /*TODO*/
        // driver_willow,	/*  6/1989 (c) 1989 (Japan) */
        /*TODO*/
        // driver_willowj,	/*  6/1989 (c) 1989 (Japan) */
        /*TODO*/
        // driver_unsquad,	/*  8/1989 (c) 1989 */
        /*TODO*/
        // driver_area88,	/*  8/1989 (c) 1989 */
        /*TODO*/
        // driver_ffight,	/* 12/1989 (c) (World) */
        /*TODO*/
        // driver_ffightu,	/* 12/1989 (c) (US)    */
        /*TODO*/
        // driver_ffightj,	/* 12/1989 (c) (Japan) */
        /*TODO*/
        // driver_1941,		/*  2/1990 (c) 1990 (World) */
        /*TODO*/
        // driver_1941j,		/*  2/1990 (c) 1990 (Japan) */
        /*TODO*/
        // driver_mercs,		/*  3/ 2/1990 (c) 1990 (World) */
        /*TODO*/
        // driver_mercsu,	/*  3/ 2/1990 (c) 1990 (US)    */
        /*TODO*/
        // driver_mercsj,	/*  3/ 2/1990 (c) 1990 (Japan) */
        /*TODO*/
        // driver_mtwins,	/*  6/19/1990 (c) 1990 (World) */
        /*TODO*/
        // driver_chikij,	/*  6/19/1990 (c) 1990 (Japan) */
        /*TODO*/
        // driver_msword,	/*  7/25/1990 (c) 1990 (World) */
        /*TODO*/
        // driver_mswordu,	/*  7/25/1990 (c) 1990 (US)    */
        /*TODO*/
        // driver_mswordj,	/*  6/23/1990 (c) 1990 (Japan) */
        /*TODO*/
        // driver_cawing,	/* 10/12/1990 (c) 1990 (World) */
        /*TODO*/
        // driver_cawingj,	/* 10/12/1990 (c) 1990 (Japan) */
        /*TODO*/
        // driver_nemo,		/* 11/30/1990 (c) 1990 (World) */
        /*TODO*/
        // driver_nemoj,		/* 11/20/1990 (c) 1990 (Japan) */
        /*TODO*/
        // driver_sf2,		/*  2/14/1991 (c) 1991 (World) */
        /*TODO*/
        // driver_sf2a,		/*  2/ 6/1991 (c) 1991 (US)    */
        /*TODO*/
        // driver_sf2b,		/*  2/14/1991 (c) 1991 (US)    */
        /*TODO*/
        // driver_sf2e,		/*  2/28/1991 (c) 1991 (US)    */
        /*TODO*/
        // driver_sf2j,		/* 12/10/1991 (c) 1991 (Japan) */
        /*TODO*/
        // driver_sf2jb,		/*  2/14/1991 (c) 1991 (Japan) */
        /*TODO*/
        // driver_3wonders,	/*  5/20/1991 (c) 1991 (US) */
        /*TODO*/
        // driver_wonder3,	/*  5/20/1991 (c) 1991 (Japan) */
        /*TODO*/
        // driver_kod,		/*  7/11/1991 (c) 1991 (World) */
        /*TODO*/
        // driver_kodj,		/*  8/ 5/1991 (c) 1991 (Japan) */
        /*TODO*/
        // driver_kodb,		/* bootleg */
        /*TODO*/
        // driver_captcomm,	/* 10/14/1991 (c) 1991 (World) */
        /*TODO*/
        // driver_captcomu,	/*  9/28/1991 (c) 1991 (US)    */
        /*TODO*/
        // driver_captcomj,	/* 12/ 2/1991 (c) 1991 (Japan) */
        /*TODO*/
        // driver_knights,	/* 11/27/1991 (c) 1991 (World) */
        /*TODO*/
        // driver_knightsu,	/* 11/27/1991 (c) 1991 (US)    */
        /*TODO*/
        // driver_knightsj,	/* 11/27/1991 (c) 1991 (Japan) */
        /*TODO*/
        // driver_sf2ce,		/*  3/13/1992 (c) 1992 (World) */
        /*TODO*/
        // driver_sf2cea,	/*  3/13/1992 (c) 1992 (US)    */
        /*TODO*/
        // driver_sf2ceb,	/*  5/13/1992 (c) 1992 (US)    */
        /*TODO*/
        // driver_sf2cej,	/*  5/13/1992 (c) 1992 (Japan) */
        /*TODO*/
        // driver_sf2rb,		/* hack */
        /*TODO*/
        // driver_sf2red,	/* hack */
        /*TODO*/
        // driver_sf2accp2,	/* hack */
        /*TODO*/
        // driver_varth,		/*  6/12/1992 (c) 1992 (World) */
        /*TODO*/
        // driver_varthu,	/*  6/12/1992 (c) 1992 (US) */
        /*TODO*/
        // driver_varthj,	/*  7/14/1992 (c) 1992 (Japan) */
        /*TODO*/
        // driver_cworld2j,	/*  6/11/1992 (QUIZ 5) (c) 1992 (Japan) */
        /*TODO*/
        // driver_wof,		/* 10/ 2/1992 (c) 1992 (World) (CPS1 + QSound) */
        /*TODO*/
        // driver_wofa,		/* 10/ 5/1992 (c) 1992 (Asia)  (CPS1 + QSound) */
        /*TODO*/
        // driver_wofj,		/* 10/31/1992 (c) 1992 (Japan) (CPS1 + QSound) */
        /*TODO*/
        // driver_sf2t,		/* 12/ 9/1992 (c) 1992 (US)    */
        /*TODO*/
        // driver_sf2tj,		/* 12/ 9/1992 (c) 1992 (Japan) */
        /*TODO*/
        // driver_dino,		/*  2/ 1/1993 (c) 1993 (World) (CPS1 + QSound) */
        /*TODO*/
        // driver_dinoj,		/*  2/ 1/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        /*TODO*/
        // driver_punisher,	/*  4/22/1993 (c) 1993 (World) (CPS1 + QSound) */
        /*TODO*/
        // driver_punishru,	/*  4/22/1993 (c) 1993 (US)    (CPS1 + QSound) */
        /*TODO*/
        // driver_punishrj,	/*  4/22/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        /*TODO*/
        // driver_slammast,	/*  7/13/1993 (c) 1993 (World) (CPS1 + QSound) */
        /*TODO*/
        // driver_slammasu,	/*  7/13/1993 (c) 1993 (US)    (CPS1 + QSound) */
        /*TODO*/
        // driver_mbomberj,	/*  7/13/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        /*TODO*/
        // driver_mbombrd,	/* 12/ 6/1993 (c) 1993 (World) (CPS1 + QSound) */
        /*TODO*/
        // driver_mbombrdj,	/* 12/ 6/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        /*TODO*/
        // driver_pnickj,	/*  6/ 8/1994 (c) 1994 + Compile license (Japan) not listed on Capcom's site
        // */
        /*TODO*/
        // driver_qad,		/*  7/ 1/1992 (c) 1992 (US)    */
        /*TODO*/
        // driver_qadj,		/*  9/21/1994 (c) 1994 (Japan) */
        /*TODO*/
        // driver_qtono2,	/*  1/23/1995 (c) 1995 (Japan) */
        /*TODO*/
        // driver_pang3,		/*  5/11/1995 (c) 1995 Mitchell (Euro) not listed on Capcom's site */
        /*TODO*/
        // driver_pang3j,	/*  5/11/1995 (c) 1995 Mitchell (Japan) not listed on Capcom's site */
        /*TODO*/
        // driver_megaman,	/* 10/ 6/1995 (c) 1995 (Asia)  */
        /*TODO*/
        // driver_rockmanj,	/*  9/22/1995 (c) 1995 (Japan) */
        /// *TODO*///driver_sfzch,		/* 10/20/1995 (c) 1995 (Japan) (CPS Changer) */

        /* Capcom CPS2 games */
        /* list completed by CPS2Shock */
        /* http://cps2shock.retrogames.com */
        /*TODO*/
        // TESTDRIVER( ssf2,		/* 11/09/1993 Super Street Fighter 2: The New Challengers (USA) */
        /*TODO*/
        // TESTDRIVER( ssf2a,		/* 11/09/1993 Super Street Fighter 2: The New Challengers (Asia) */
        /*TODO*/
        // TESTDRIVER( ssf2j,		/* 10/09/1993 Super Street Fighter 2: The New Challengers (Japan) */
        /*TODO*/
        // TESTDRIVER( ecofe,		/* 03/12/1993 Eco Fighters (Etc) */
        /*TODO*/
        // TESTDRIVER( uecology,	/* 03/12/1993 Ultimate Ecology (Japan) */
        /*TODO*/
        // TESTDRIVER( ddtod,		/* 13/01/1994 Dungeons & Dragons: Tower of Doom (USA) */
        /*TODO*/
        // TESTDRIVER( ddtoda,	/* 13/01/1994 Dungeons & Dragons: Tower of Doom (Asia) */
        /*TODO*/
        // TESTDRIVER( ddtodr1,	/* 25/01/1994 Dungeons & Dragons: Tower of Doom (USA) */
        /*TODO*/
        // TESTDRIVER( ssf2t,		/* 23/02/1994 Super Street Fighter 2 Turbo (USA) */
        /*TODO*/
        // TESTDRIVER( ssf2xj,	/* 23/02/1994 Super Street Fighter 2 X: Grand Master Challenge (Japan) */
        /*TODO*/
        // TESTDRIVER( avsp,		/* 20/05/1994 Aliens Vs. Predator (USA) */
        /*TODO*/
        // TESTDRIVER( avspa,		/* 20/05/1994 Aliens Vs. Predator (Asia) */
        /*TODO*/
        // TESTDRIVER( vampj,		/* 05/07/1994 Vampire: The Night Warriors (Japan) */
        /*TODO*/
        // TESTDRIVER( vampa,		/* 05/07/1994 Vampire: The Night Warriors (Asia) */
        /*TODO*/
        // TESTDRIVER( dstlk,		/* 18/08/1994 DarkStalkers: The Night Warriors (USA) */
        /*TODO*/
        // TESTDRIVER( slam2e,	/* 02/09/1994 Saturday Night Slammasters II: Ring of Destruction (Euro)
        // */
        /*TODO*/
        // TESTDRIVER( smbomber,	/* 08/08/1994 Super Muscle Bomber: The International Blowout (Japan) */
        /*TODO*/
        // TESTDRIVER( armwar,	/* 24/10/1994 Armoured Warriors (USA) */
        /*TODO*/
        // TESTDRIVER( armwara,	/* 20/09/1994 Armoured Warriors (Asia) */
        /*TODO*/
        // TESTDRIVER( xmcotaj,	/* 19/12/1994 X-Men: Children of the Atom (Japan) */
        /*TODO*/
        // TESTDRIVER( xmcota,	/* 05/01/1995 X-Men: Children of the Atom (USA) */
        /*TODO*/
        // TESTDRIVER( vhuntj,	/* 02/03/1995 Vampire Hunter: Darkstalkers 2 (Japan) */
        /*TODO*/
        // TESTDRIVER( nwarr,		/* 06/04/1995 Night Warriors: DarkStalkers Revenge (USA) */
        /*TODO*/
        // TESTDRIVER( cybotsj,	/* 20/04/1995 Cyberbots: Full Metal Madness (Japan) */
        /*TODO*/
        // TESTDRIVER( sfa,		/* 27/06/1995 Street Fighter Alpha: The Warriors Dream (USA) */
        /*TODO*/
        // TESTDRIVER( sfar1,		/* 27/07/1995 Street Fighter Alpha: The Warriors Dream (USA) */
        /*TODO*/
        // TESTDRIVER( sfzj,		/* 27/06/1995 Street Fighter Zero (Japan) */
        /*TODO*/
        // TESTDRIVER( sfzjr1,	/* 27/07/1995 Street Fighter Zero (Japan) */
        /*TODO*/
        // TESTDRIVER( msh,		/* 24/10/1995 Marvel Super Heroes (USA) */
        /*TODO*/
        // TESTDRIVER( 19xx,		/* 07/12/1995 19XX: The Battle Against Destiny (USA) */
        /*TODO*/
        // TESTDRIVER( ddsom,		/* 09/02/1996 Dungeons & Dragons 2: Shadow over Mystara (USA) */
        /*TODO*/
        // TESTDRIVER( sfz2j,		/* 27/02/1996 Street Fighter Zero 2 (Japan) */
        /*TODO*/
        // TESTDRIVER( spf2xj,	/* 31/05/1996 Super Puzzle Fighter 2 X (Japan) */
        /*TODO*/
        // TESTDRIVER( spf2t,		/* 20/06/1996 Super Puzzle Fighter 2 Turbo (USA) */
        /*TODO*/
        // TESTDRIVER( qndream,	/* 26/06/1996 Quiz Nanairo Dreams: Miracle of the Rainbow Colored Town
        // (Japan) */
        /*TODO*/
        // TESTDRIVER( rckman2j,	/* 08/07/1996 Rockman 2: The Power Fighters (Japan) */
        /*TODO*/
        // TESTDRIVER( sfz2a,		/* 05/08/1996 Street Fighter Zero 2 Alpha (Japan) */
        /*TODO*/
        // TESTDRIVER( xmvsf,		/* 04/10/1996 X-Men Vs. Street Fighter (USA) */
        /*TODO*/
        // TESTDRIVER( batcirj,	/* 19/03/1997 Battle Circuit (Japan) */
        /*TODO*/
        // TESTDRIVER( batcira,	/* 19/03/1997 Battle Circuit (Asia) */
        /*TODO*/
        // TESTDRIVER( vsav,		/* 19/05/1997 Vampire Savior: The Lord of Vampire (USA) */
        /*TODO*/
        // TESTDRIVER( vsavj,		/* 19/05/1997 Vampire Savior: The Lord of Vampire (Japan) */
        /*TODO*/
        // TESTDRIVER( mshvsf,	/* 25/06/1997 Marvel Super Heroes Vs. Street Fighter (USA) */
        /*TODO*/
        // TESTDRIVER( mshvsfj,	/* 07/07/1997 Marvel Super Heroes Vs. Street Fighter (Japan) */
        /*TODO*/
        // TESTDRIVER( sgemf,		/* 04/09/1997 Super Gem Fighter Mini Mix (USA) */
        /*TODO*/
        // TESTDRIVER( pfghtj,	/* 04/09/1997 Pocket Fighter (Japan) */
        /*TODO*/
        // TESTDRIVER( vhunt2,	/* 13/09/1997 Vampire Hunter 2: Darkstalkers Revenge (Japan) */
        /*TODO*/
        // TESTDRIVER( vsav2,		/* 13/09/1997 Vampire Savior 2: The Lord of Vampire (Japan) */
        /*TODO*/
        // TESTDRIVER( mvsc,		/* 23/01/1998 Marvel Super Heroes vs. Capcom: Clash of Super Heroes (USA)
        // */
        /*TODO*/
        // TESTDRIVER( mvscj,		/* 23/01/1998 Marvel Super Heroes vs. Capcom: Clash of Super Heroes
        // (Japan) */
        /*TODO*/
        // TESTDRIVER( sfa3,		/* 29/06/1998 Street Fighter Alpha 3 (USA) */
        /*TODO*/
        // TESTDRIVER( sfz3,		/* 27/07/1998 Street Fighter Zero 3 (USA) */
        /* Giga Wing */
        /* Gulum Pa! */
        /* Dimahoo */
        /* Capcom CPS3 games */
        /* 10/1996 Warzard */
        /*  2/1997 Street Fighter III - New Generation */
        /* ???? Jojo's Bizarre Adventure */
        /* ???? Street Fighter 3: Second Impact ~giant attack~ */
        /* ???? Street Fighter 3: Third Strike ~fight to the finish~ */
        /* Capcom ZN1 */
        /*TODO*/
        // TESTDRIVER( ts2j,		/*  Battle Arena Toshinden 2 (JAPAN 951124) */
        /*  7/1996 Star Gladiator */
        /*TODO*/
        // TESTDRIVER( sfex,		/*  Street Fighter EX (ASIA 961219) */
        /*TODO*/
        // TESTDRIVER( sfexj,		/*  Street Fighter EX (JAPAN 961130) */
        /*TODO*/
        // TESTDRIVER( sfexp,		/*  Street Fighter EX Plus (USA 970311) */
        /*TODO*/
        // TESTDRIVER( sfexpj,	/*  Street Fighter EX Plus (JAPAN 970311) */
        /*TODO*/
        // TESTDRIVER( rvschool,	/*  Rival Schools (ASIA 971117) */
        /*TODO*/
        // TESTDRIVER( jgakuen,	/*  Justice Gakuen (JAPAN 971117) */

        /* Capcom ZN2 */
        /*TODO*/
        // TESTDRIVER( sfex2,		/*  Street Fighter EX 2 (JAPAN 980312) */
        /*TODO*/
        // TESTDRIVER( tgmj,		/*  Tetris The Grand Master (JAPAN 980710) */
        /*TODO*/
        // TESTDRIVER( kikaioh,	/*  Kikaioh (JAPAN 980914) */
        /*TODO*/
        // TESTDRIVER( sfex2p,	/*  Street Fighter EX 2 Plus (JAPAN 990611) */
        /*TODO*/
        // TESTDRIVER( shiryu2,	/*  Strider Hiryu 2 (JAPAN 991213) */
        /*  Star Gladiator 2 */
        /*  Rival Schools 2 */
        /* Video System ZN1 */
        /*TODO*/
        // TESTDRIVER( sncwgltd,	/*  Sonic Wings Limited (JAPAN) */

        /* Tecmo ZN1 */
        /*TODO*/
        // TESTDRIVER( glpracr2,	/*  Gallop Racer 2 (JAPAN) */
        /*TODO*/
        // TESTDRIVER( doapp,		/*  Dead Or Alive ++ (JAPAN) */

        /* Incredible Technologies games */
        driver_capbowl, /* (c) 1988 Incredible Technologies */
        driver_capbowl2, /* (c) 1988 Incredible Technologies */
        driver_clbowl, /* (c) 1989 Incredible Technologies */
        driver_bowlrama, /* (c) 1991 P & P Marketing */
        /*
    The Incredible Technologies game list
    http://www.itsgames.com/it/CorporateProfile/corporateprofile_main.htm

    ShuffleShot - (Incredible Technologies, Inc.)
    Peter Jacobsen's Golden Tee '97 - (Incredible Technologies, Inc.)
    World Class Bowling - (Incredible Technologies, Inc.)
    Peter Jacobsen's Golden Tee 3D Golf - (Incredible Technologies, Inc.)
    Street Fighter - "The Movie" (Capcom)
    PAIRS - (Strata)
    BloodStorm - (Strata)
    Driver's Edge - (Strata)
    NFL Hard Yardage - (Strata)
    Time Killers - (Strata)
    Neck 'n' Neck - (Bundra Games)
    Ninja Clowns - (Strata)
    Rim Rockin' Basketball - (Strata)
    Arlington Horse Racing - (Strata)
    Dyno Bop - (Grand Products)
    Poker Dice - (Strata)
    Peggle - (Strata)
    Slick Shot - (Grand Products)
    Golden Tee Golf II - (Strata)
    Hot Shots Tennis - (Strata)
    Strata Bowling - (Strata)
    Golden Tee Golf I - (Strata)
    Capcom Bowling - (Strata)
         */
        /* Leland games */
        driver_redlin2p, /* (c) 1987 Cinematronics + Tradewest license */
        driver_quarterb, /* (c) 1987 Leland */
        driver_quartrba, /* (c) 1987 Leland */
        driver_viper, /* (c) 1988 Leland */
        driver_teamqb, /* (c) 1988 Leland */
        driver_teamqb2, /* (c) 1988 Leland */
        driver_aafb, /* (c) 1989 Leland */
        driver_aafbd2p, /* (c) 1989 Leland */
        driver_aafbb, /* (c) 1989 Leland */
        driver_offroad, /* (c) 1989 Leland */
        driver_offroadt, /* (c) 1989 Leland */
        driver_pigout, /* (c) 1990 Leland */
        driver_pigouta, /* (c) 1990 Leland */
        /*TODO*/
        // driver_ataxx,		/* (c) 1990 Leland */
        /*TODO*/
        // driver_ataxxa,	/* (c) 1990 Leland */
        /*TODO*/
        // driver_ataxxj,	/* (c) 1990 Leland */
        /*TODO*/
        // driver_wsf,		/* (c) 1990 Leland */
        /*TODO*/
        // driver_indyheat,	/* (c) 1991 Leland */
        /*TODO*/
        // driver_brutforc,	/* (c) 1991 Leland */

        /* Gremlin 8080 games */
        /* the numbers listed are the range of ROM part numbers */
        /*TODO*/
        // driver_blockade,	/* 1-4 [1977 Gremlin] */
        /*TODO*/
        // driver_comotion,	/* 5-7 [1977 Gremlin] */
        /*TODO*/
        // driver_hustle,	/* 16-21 [1977 Gremlin] */
        /*TODO*/
        // driver_blasto,	/* [1978 Gremlin] */

        /* other Sega 8-bit games */
        driver_turbo, /* (c) 1981 Sega */
        driver_turboa, /* (c) 1981 Sega */
        driver_turbob, /* (c) 1981 Sega */
        /*TODO*/
        // TESTDRIVER( kopunch,	/* 834-0103 (c) 1981 Sega */
        /* Sega System 16 games */
        // Not working
        /*TODO*/
        // driver_alexkidd,	/* (c) 1986 (protected) */
        /*TODO*/
        // driver_aliensya,	/* (c) 1987 (protected) */
        /*TODO*/
        // driver_aliensyb,	/* (c) 1987 (protected) */
        /*TODO*/
        // driver_aliensyj,	/* (c) 1987 (protected. Japan) */
        /*TODO*/
        // driver_astorm,	/* (c) 1990 (protected) */
        /*TODO*/
        // driver_astorm2p,	/* (c) 1990 (protected 2 Players) */
        /*TODO*/
        // driver_auraila,	/* (c) 1990 Sega / Westone (protected) */
        /*TODO*/
        // driver_bayrouta,	/* (c) 1989 (protected) */
        /*TODO*/
        // driver_bayrtbl1,	/* (c) 1989 (protected) (bootleg) */
        /*TODO*/
        // driver_bayrtbl2,	/* (c) 1989 (protected) (bootleg) */
        /*TODO*/
        // driver_enduror,	/* (c) 1985 (protected) */
        /*TODO*/
        // driver_eswat,		/* (c) 1989 (protected) */
        /*TODO*/
        // driver_fpoint,	/* (c) 1989 (protected) */
        /*TODO*/
        // driver_goldnaxb,	/* (c) 1989 (protected) */
        /*TODO*/
        // driver_goldnaxc,	/* (c) 1989 (protected) */
        /*TODO*/
        // driver_goldnaxj,	/* (c) 1989 (protected. Japan) */
        /*TODO*/
        // driver_jyuohki,	/* (c) 1988 (protected. Altered Beast Japan) */
        /*TODO*/
        // driver_moonwalk,	/* (c) 1990 (protected) */
        /*TODO*/
        // driver_moonwlka,	/* (c) 1990 (protected) */
        /*TODO*/
        // driver_passsht,	/* (protected) */
        /*TODO*/
        // driver_sdioj,		/* (c) 1987 (protected. Japan) */
        /*TODO*/
        // driver_shangon,	/* (c) 1992 (protected) */
        /*TODO*/
        // driver_shinobia,	/* (c) 1987 (protected) */
        /*TODO*/
        // driver_shinobib,	/* (c) 1987 (protected) */
        /*TODO*/
        // driver_tetris,	/* (c) 1988 (protected) */
        /*TODO*/
        // driver_tetrisa,	/* (c) 1988 (protected) */
        /*TODO*/
        // driver_wb3a,		/* (c) 1988 Sega / Westone (protected) */

        /*TODO*/
        // TESTDRIVER( aceattac,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( aburner,	/* */
        /*TODO*/
        // TESTDRIVER( aburner2,  /* */
        /*TODO*/
        // TESTDRIVER( afighter,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( bloxeed,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( cltchitr,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( cotton,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( cottona,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( ddcrew,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( dunkshot,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( exctleag,  /* (protected) */
        /*TODO*/
        // TESTDRIVER( lghost,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( loffire,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( mvp,		/* (protected) */
        /*TODO*/
        // TESTDRIVER( ryukyu,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( suprleag,  /* (protected) */
        /*TODO*/
        // TESTDRIVER( thndrbld,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( thndrbdj,  /* (protected?) */
        /*TODO*/
        // TESTDRIVER( toutrun,	/* (protected) */
        /*TODO*/
        // TESTDRIVER( toutruna,	/* (protected) */

        // Working
        /*TODO*/
        // driver_alexkida,	/* (c) 1986 */
        /*TODO*/
        // driver_aliensyn,	/* (c) 1987 */
        /*TODO*/
        // driver_altbeas2,	/* (c) 1988 */
        /*TODO*/
        // driver_altbeast,	/* (c) 1988 */
        /*TODO*/
        // driver_astormbl,	/* bootleg */
        /*TODO*/
        // driver_atomicp,	/* (c) 1990 Philko */
        /*TODO*/
        // driver_aurail,	/* (c) 1990 Sega / Westone */
        /*TODO*/
        // driver_bayroute,	/* (c) 1989 */
        /*TODO*/
        // driver_bodyslam,	/* (c) 1986 */
        /*TODO*/
        // driver_dduxbl,	/* (c) 1989 (Datsu bootleg) */
        /*TODO*/
        // driver_dumpmtmt,	/* (c) 1986 (Japan) */
        /*TODO*/
        // driver_endurob2,	/* (c) 1985 (Beta bootleg) */
        /*TODO*/
        // driver_endurobl,	/* (c) 1985 (Herb bootleg) */
        /*TODO*/
        // driver_eswatbl,	/* (c) 1989 (but bootleg) */
        /*TODO*/
        // driver_fantzone,	/* (c) 1986 */
        /*TODO*/
        // driver_fantzono,	/* (c) 1986 */
        /*TODO*/
        // driver_fpointbl,	/* (c) 1989 (Datsu bootleg) */
        /*TODO*/
        // driver_goldnabl,	/* (c) 1989 (bootleg) */
        /*TODO*/
        // driver_goldnaxa,	/* (c) 1989 */
        /*TODO*/
        // driver_goldnaxe,	/* (c) 1989 */
        /*TODO*/
        // driver_hangon,	/* (c) 1985 */
        /*TODO*/
        // driver_hwchamp,	/* (c) 1987 */
        /*TODO*/
        // driver_mjleague,	/* (c) 1985 */
        /*TODO*/
        // driver_moonwlkb,	/* bootleg */
        /*TODO*/
        // driver_outrun,	/* (c) 1986 (bootleg)*/
        /*TODO*/
        // driver_outruna,	/* (c) 1986 (bootleg) */
        /*TODO*/
        // driver_outrunb,	/* (c) 1986 (protected beta bootleg) */
        /*TODO*/
        // driver_passht4b,	/* bootleg */
        /*TODO*/
        // driver_passshtb,	/* bootleg */
        /*TODO*/
        // driver_quartet,	/* (c) 1986 */
        /*TODO*/
        // driver_quartet2,	/* (c) 1986 */
        /*TODO*/
        // driver_quartetj,	/* (c) 1986 */
        /*TODO*/
        // driver_riotcity,	/* (c) 1991 Sega / Westone */
        /*TODO*/
        // driver_sdi,		/* (c) 1987 */
        /*TODO*/
        // driver_shangonb,	/* (c) 1992 (but bootleg) */
        /*TODO*/
        // driver_sharrier,	/* (c) 1985 */
        /*TODO*/
        // driver_shdancbl,	/* (c) 1989 (but bootleg) */
        /*TODO*/
        // driver_shdancer,	/* (c) 1989 */
        /*TODO*/
        // driver_shdancrj,	/* (c) 1989 */
        /*TODO*/
        // driver_shinobi,	/* (c) 1987 */
        /*TODO*/
        // driver_shinobl,	/* (c) 1987 (but bootleg) */
        /*TODO*/
        // driver_tetrisbl,	/* (c) 1988 (but bootleg) */
        /*TODO*/
        // driver_timscanr,	/* (c) 1987 */
        /*TODO*/
        // driver_toryumon,	/* (c) 1995 */
        /*TODO*/
        // driver_tturf,		/* (c) 1989 Sega / Sunsoft */
        /*TODO*/
        // driver_tturfbl,	/* (c) 1989 (Datsu bootleg) */
        /*TODO*/
        // driver_tturfu,	/* (c) 1989 Sega / Sunsoft */
        /*TODO*/
        // driver_wb3,		/* (c) 1988 Sega / Westone */
        /*TODO*/
        // driver_wb3bl,		/* (c) 1988 Sega / Westone (but bootleg) */
        /*TODO*/
        // driver_wrestwar,	/* (c) 1989 */

        /* Deniam games */
        /* they run on Sega System 16 video hardware */
        /*TODO*/
        // driver_logicpro,	/* (c) 1996 Deniam */
        /*TODO*/
        // driver_karianx,	/* (c) 1996 Deniam */
        /*TODO*/
        // driver_logicpr2,	/* (c) 1997 Deniam (Japan) */
        /*
    Deniam is a Korean company (http://deniam.co.kr).

    Game list:
    Title            System     Date
    ---------------- ---------- ----------
    GO!GO!           deniam-16b 1995/10/11
    Logic Pro        deniam-16b 1996/10/20
    Karian Cross     deniam-16b 1997/04/17
    LOTTERY GAME     deniam-16c 1997/05/21
    Logic Pro 2      deniam-16c 1997/06/20
    Propose          deniam-16c 1997/06/21
    BOMULEUL CHAJARA SEGA ST-V  1997/04/11
         */
        /* Data East "Burger Time hardware" games */
        /*TODO*/
        // driver_lnc,		/* (c) 1981 */
        /*TODO*/
        // driver_zoar,		/* (c) 1982 */
        driver_btime, /* (c) 1982 */
        driver_btime2, /* (c) 1982 */
        driver_btimem, /* (c) 1982 + Midway */
        driver_wtennis, /* bootleg 1982 */
        driver_brubber, /* (c) 1982 */
        driver_bnj, /* (c) 1982 + Midway */
        driver_caractn, /* bootleg */
        driver_disco, /* (c) 1982 */
        driver_mmonkey, /* (c) 1982 Technos Japan + Roller Tron */
        /* cassette system */
        /*TODO*/
        // TESTDRIVER( decocass,
        driver_cookrace, /* bootleg */
        /* other Data East games */
        /*TODO*/
        // driver_astrof,	/* (c) [1980?] */
        /*TODO*/
        // driver_astrof2,	/* (c) [1980?] */
        /*TODO*/
        // driver_astrof3,	/* (c) [1980?] */
        /*TODO*/
        // driver_tomahawk,	/* (c) [1980?] */
        /*TODO*/
        // driver_tomahaw5,	/* (c) [1980?] */
        driver_firetrap, /* (c) 1986 */
        driver_firetpbl, /* bootleg */
        driver_shootout, /* (c) 1985 Data East USA (US) */
        driver_shootouj, /* (c) 1985 Data East USA (Japan) */
        driver_shootoub, /* bootleg */
        driver_exprraid, /* (c) 1986 Data East USA (US) */
        driver_wexpress, /* (c) 1986 Data East Corporation (World?) */
        driver_wexpresb, /* bootleg */
        driver_pcktgal, /* (c) 1987 Data East Corporation (Japan) */
        driver_pcktgalb, /* bootleg */
        driver_pcktgal2, /* (c) 1989 Data East Corporation (World?) */
        driver_spool3, /* (c) 1989 Data East Corporation (World?) */
        driver_spool3i, /* (c) 1990 Data East Corporation + I-Vics license */
        driver_battlera, /* (c) 1988 Data East Corporation (World) */
        driver_bldwolf, /* (c) 1988 Data East USA (US) */
        driver_actfancr, /* (c) 1989 Data East Corporation (World) */
        driver_actfanc1, /* (c) 1989 Data East Corporation (World) */
        driver_actfancj, /* (c) 1989 Data East Corporation (Japan) */
        driver_triothep, /* (c) 1989 Data East Corporation (Japan) */
        /* Data East 8-bit games */
        driver_lastmiss, /* (c) 1986 Data East USA (US) */
        driver_lastmss2, /* (c) 1986 Data East USA (US) */
        driver_shackled, /* (c) 1986 Data East USA (US) */
        driver_breywood, /* (c) 1986 Data East Corporation (Japan) */
        driver_csilver, /* (c) 1987 Data East Corporation (Japan) */
        driver_ghostb, /* (c) 1987 Data East USA (US) */
        driver_ghostb3, /* (c) 1987 Data East USA (US) */
        driver_meikyuh, /* (c) 1987 Data East Corporation (Japan) */
        driver_srdarwin, /* (c) 1987 Data East Corporation (Japan) */
        driver_gondo, /* (c) 1987 Data East USA (US) */
        driver_makyosen, /* (c) 1987 Data East Corporation (Japan) */
        driver_garyoret, /* (c) 1987 Data East Corporation (Japan) */
        driver_cobracom, /* (c) 1988 Data East Corporation (World) */
        driver_cobracmj, /* (c) 1988 Data East Corporation (Japan) */
        driver_oscar, /* (c) 1988 Data East USA (US) */
        driver_oscarj, /* (c) 1987 Data East Corporation (Japan) */
        driver_oscarj1, /* (c) 1987 Data East Corporation (Japan) */
        driver_oscarj0, /* (c) 1987 Data East Corporation (Japan) */
        /* Data East 16-bit games */
        /*TODO*/
        // driver_karnov,	/* (c) 1987 Data East USA (US) */
        /*TODO*/
        // driver_karnovj,	/* (c) 1987 Data East Corporation (Japan) */
        /*TODO*/
        // TESTDRIVER( wndrplnt,	/* (c) 1987 Data East Corporation (Japan) */
        /*TODO*/
        // driver_chelnov,	/* (c) 1988 Data East USA (US) */
        /*TODO*/
        // driver_chelnovj,	/* (c) 1988 Data East Corporation (Japan) */
        /* the following ones all run on similar hardware */
        /*TODO*/
        // driver_hbarrel,	/* (c) 1987 Data East USA (US) */
        /*TODO*/
        // driver_hbarrelw,	/* (c) 1987 Data East Corporation (World) */
        /*TODO*/
        // driver_baddudes,	/* (c) 1988 Data East USA (US) */
        /*TODO*/
        // driver_drgninja,	/* (c) 1988 Data East Corporation (Japan) */
        /*TODO*/
        // TESTDRIVER( birdtry,	/* (c) 1988 Data East Corporation (Japan) */
        /*TODO*/
        // driver_robocop,	/* (c) 1988 Data East Corporation (World) */
        /*TODO*/
        // driver_robocopu,	/* (c) 1988 Data East USA (US) */
        /*TODO*/
        // driver_robocpu0,	/* (c) 1988 Data East USA (US) */
        /*TODO*/
        // driver_robocopb,	/* bootleg */
        /*TODO*/
        // driver_hippodrm,	/* (c) 1989 Data East USA (US) */
        /*TODO*/
        // driver_ffantasy,	/* (c) 1989 Data East Corporation (Japan) */
        /*TODO*/
        // driver_slyspy,	/* (c) 1989 Data East USA (US) */
        /*TODO*/
        // driver_slyspy2,	/* (c) 1989 Data East USA (US) */
        /*TODO*/
        // driver_secretag,	/* (c) 1989 Data East Corporation (World) */
        /*TODO*/
        // TESTDRIVER( secretab,	/* bootleg */
        /*TODO*/
        // driver_midres,	/* (c) 1989 Data East Corporation (World) */
        /*TODO*/
        // driver_midresu,	/* (c) 1989 Data East USA (US) */
        /*TODO*/
        // driver_midresj,	/* (c) 1989 Data East Corporation (Japan) */
        /*TODO*/
        // driver_bouldash,	/* (c) 1990 Data East Corporation */
        /* end of similar hardware */
        /*TODO*/
        // driver_stadhero,	/* (c) 1988 Data East Corporation (Japan) */
        /*TODO*/
        // driver_madmotor,	/* (c) [1989] Mitchell */
        /* All these games have a unique code stamped on the mask roms */
        /*TODO*/
        // driver_vaportra,	/* MAA (c) 1989 Data East Corporation (World) */
        /*TODO*/
        // driver_vaportru,	/* MAA (c) 1989 Data East Corporation (US) */
        /*TODO*/
        // driver_kuhga,		/* MAA (c) 1989 Data East Corporation (Japan) */
        /*TODO*/
        // driver_cbuster,	/* MAB (c) 1990 Data East Corporation (World) */
        /*TODO*/
        // driver_cbusterw,	/* MAB (c) 1990 Data East Corporation (World) */
        /*TODO*/
        // driver_cbusterj,	/* MAB (c) 1990 Data East Corporation (Japan) */
        /*TODO*/
        // driver_twocrude,	/* MAB (c) 1990 Data East USA (US) */
        /*TODO*/
        // driver_darkseal,	/* MAC (c) 1990 Data East Corporation (World) */
        /*TODO*/
        // driver_darksea1,	/* MAC (c) 1990 Data East Corporation (World) */
        /*TODO*/
        // driver_darkseaj,	/* MAC (c) 1990 Data East Corporation (Japan) */
        /*TODO*/
        // driver_gatedoom,	/* MAC (c) 1990 Data East Corporation (US) */
        /*TODO*/
        // driver_gatedom1,	/* MAC (c) 1990 Data East Corporation (US) */
        /*TODO*/
        // TESTDRIVER( edrandy,	/* MAD (c) 1990 Data East Corporation (World) */
        /*TODO*/
        // TESTDRIVER( edrandyj,	/* MAD (c) 1990 Data East Corporation (Japan) */
        /*TODO*/
        // driver_supbtime,	/* MAE (c) 1990 Data East Corporation (World) */
        /*TODO*/
        // driver_supbtimj,	/* MAE (c) 1990 Data East Corporation (Japan) */
        /* Mutant Fighter/Death Brade MAF (c) 1991 */
        /*TODO*/
        // driver_cninja,	/* MAG (c) 1991 Data East Corporation (World) */
        /*TODO*/
        // driver_cninja0,	/* MAG (c) 1991 Data East Corporation (World) */
        /*TODO*/
        // driver_cninjau,	/* MAG (c) 1991 Data East Corporation (US) */
        /*TODO*/
        // driver_joemac,	/* MAG (c) 1991 Data East Corporation (Japan) */
        /*TODO*/
        // driver_stoneage,	/* bootleg */
        /* Robocop 2           MAH (c) 1991 */
        /* Desert Assault/Thunderzone MAJ (c) 1991 */
        /* Rohga Armour Attack/Wolf Fang MAM (c) 1991 */
        /* Captain America     MAN (c) 1991 */
        /*TODO*/
        // driver_tumblep,	/* MAP (c) 1991 Data East Corporation (World) */
        /*TODO*/
        // driver_tumblepj,	/* MAP (c) 1991 Data East Corporation (Japan) */
        /*TODO*/
        // driver_tumblepb,	/* bootleg */
        /*TODO*/
        // driver_tumblep2,	/* bootleg */
        /* Dragon Gun/Dragoon  MAR (c) 1992 */
        /* Wizard's Fire       MAS (c) 1992 */
        /*TODO*/
        // TESTDRIVER( funkyjet,	/* MAT (c) 1992 Mitchell */
        /* Diet GoGo      	   MAY (c) 1993 */
        /* Fighter's History   MBF (c) 1993 */
        /* Joe & Mac Return    MBN (c) 1994 */
        /* Chain Reaction      MCC (c) 1994 */
        /* Tehkan / Tecmo games (Tehkan became Tecmo in 1986) */
        /* 6009 Tank Busters */
        /* 6011 Pontoon (c) 1985 Tehkan is a gambling game - removed */
        /*TODO*/
        // driver_gaiden,	/* 6215 - (c) 1988 Tecmo (World) */
        /*TODO*/
        // driver_shadoww,	/* 6215 - (c) 1988 Tecmo (US) */
        /*TODO*/
        // driver_ryukendn,	/* 6215 - (c) 1989 Tecmo (Japan) */
        /*TODO*/
        // driver_tknight,	/* (c) 1989 Tecmo */
        /*TODO*/
        // driver_wildfang,	/* (c) 1989 Tecmo */        
        // driver_fstarfrc,	/* (c) 1992 Tecmo */
        /*TODO*/
        // driver_ginkun,	/* (c) 1995 Tecmo */

        //driver_tp84,		/* GX388 (c) 1984 */
        /*TODO*/
        // driver_tp84a,		/* GX388 (c) 1984 */
        driver_hyperspt,
        driver_hpolym84,
        driver_roadf,
        driver_roadf2,
        driver_gberet,
        driver_rushatck,
        driver_gberetb,
        driver_mrgoemon,
        driver_jackal, /* GX631 (c) 1986 (World) */
        driver_topgunr, /* GX631 (c) 1986 (US) */
        driver_jackalj, /* GX631 (c) 1986 (Japan) */
        driver_topgunbl, /* bootleg */
        /*TODO*/
        // driver_contra,	/* GX633 (c) 1987 */
        /*TODO*/
        // driver_contrab,	/* bootleg */
        /*TODO*/
        // driver_contraj,	/* GX633 (c) 1987 (Japan) */
        /*TODO*/
        // driver_contrajb,	/* bootleg */
        /*TODO*/
        // driver_gryzor,	/* GX633 (c) 1987 */
        driver_combasc, /* GX611 (c) 1988 */
        driver_combasct, /* GX611 (c) 1987 */
        driver_combascj, /* GX611 (c) 1987 (Japan) */
        driver_bootcamp, /* GX611 (c) 1987 */
        driver_combascb, /* bootleg */
        /*TODO*/
        // driver_rockrage,	/* GX620 (c) 1986 (World?) */
        /*TODO*/
        // driver_rockragj,	/* GX620 (c) 1986 (Japan) */
        /*TODO*/
        // driver_mx5000,	/* GX669 (c) 1987 */
        /*TODO*/
        // driver_flkatck,	/* GX669 (c) 1987 (Japan) */
        /*TODO*/
        // driver_fastlane,	/* GX752 (c) 1987 */
        /*TODO*/
        // driver_labyrunr,	/* GX771 (c) 1987 (Japan) */
        /*TODO*/
        // driver_thehustl,	/* GX765 (c) 1987 (Japan) */
        /*TODO*/
        // driver_thehustj,	/* GX765 (c) 1987 (Japan) */
        /*TODO*/
        // driver_rackemup,	/* GX765 (c) 1987 */
        /*TODO*/
        // driver_battlnts,	/* GX777 (c) 1987 */
        /*TODO*/
        // driver_battlntj,	/* GX777 (c) 1987 (Japan) */
        /*TODO*/
        // driver_bladestl,	/* GX797 (c) 1987 */
        /*TODO*/
        // driver_bladstle,	/* GX797 (c) 1987 */
        /*TODO*/
        // driver_hcastle,	/* GX768 (c) 1988 */
        /*TODO*/
        // driver_hcastlea,	/* GX768 (c) 1988 */
        /*TODO*/
        // driver_hcastlej,	/* GX768 (c) 1988 (Japan) */
        /*TODO*/
        // driver_ajax,		/* GX770 (c) 1987 */
        /*TODO*/
        // driver_ajaxj,		/* GX770 (c) 1987 (Japan) */
        /*TODO*/
        // driver_scontra,	/* GX775 (c) 1988 */
        /*TODO*/
        // driver_scontraj,	/* GX775 (c) 1988 (Japan) */
        /*TODO*/
        // driver_thunderx,	/* GX873 (c) 1988 */
        /*TODO*/
        // driver_thnderxj,	/* GX873 (c) 1988 (Japan) */
        /*TODO*/
        // driver_mainevt,	/* GX799 (c) 1988 */
        /*TODO*/
        // driver_mainevt2,	/* GX799 (c) 1988 */
        /*TODO*/
        // driver_ringohja,	/* GX799 (c) 1988 (Japan) */
        /*TODO*/
        // driver_devstors,	/* GX890 (c) 1988 */
        /*TODO*/
        // driver_devstor2,	/* GX890 (c) 1988 */
        /*TODO*/
        // driver_devstor3,	/* GX890 (c) 1988 */
        /*TODO*/
        // driver_garuka,	/* GX890 (c) 1988 (Japan) */
        /*TODO*/
        // driver_88games,	/* GX861 (c) 1988 */
        /*TODO*/
        // driver_konami88,	/* GX861 (c) 1988 */
        /*TODO*/
        // driver_hypsptsp,	/* GX861 (c) 1988 (Japan) */
        /*TODO*/
        // driver_gbusters,	/* GX878 (c) 1988 */
        /*TODO*/
        // driver_crazycop,	/* GX878 (c) 1988 (Japan) */
        /*TODO*/
        // driver_crimfght,	/* GX821 (c) 1989 (US) */
        /*TODO*/
        // driver_crimfgt2,	/* GX821 (c) 1989 (World) */
        /*TODO*/
        // driver_crimfgtj,	/* GX821 (c) 1989 (Japan) */
        /*TODO*/
        // driver_spy,		/* GX857 (c) 1989 (US) */
        /*TODO*/
        // driver_bottom9,	/* GX891 (c) 1989 */
        /*TODO*/
        // driver_bottom9n,	/* GX891 (c) 1989 */
        /*TODO*/
        // driver_blockhl,	/* GX973 (c) 1989 */
        /*TODO*/
        // driver_quarth,	/* GX973 (c) 1989 (Japan) */
        /*TODO*/
        // driver_aliens,	/* GX875 (c) 1990 (World) */
        /*TODO*/
        // driver_aliens2,	/* GX875 (c) 1990 (World) */
        /*TODO*/
        // driver_aliensu,	/* GX875 (c) 1990 (US) */
        /*TODO*/
        // driver_aliensj,	/* GX875 (c) 1990 (Japan) */
        /*TODO*/
        // driver_surpratk,	/* GX911 (c) 1990 (Japan) */
        /*TODO*/
        // driver_parodius,	/* GX955 (c) 1990 (Japan) */
        /*TODO*/
        // driver_rollerg,	/* GX999 (c) 1991 (US) */
        /*TODO*/
        // driver_rollergj,	/* GX999 (c) 1991 (Japan) */
        /*TODO*/
        // driver_xexex,		/* GX067 (c) 1991 (World) */
        /*TODO*/
        // driver_xexexj,	/* GX067 (c) 1991 (Japan) */
        /*TODO*/
        // driver_simpsons,	/* GX072 (c) 1991 */
        /*TODO*/
        // driver_simpsn2p,	/* GX072 (c) 1991 */
        /*TODO*/
        // driver_simps2pj,	/* GX072 (c) 1991 (Japan) */
        /*TODO*/
        // driver_vendetta,	/* GX081 (c) 1991 (Asia) */
        /*TODO*/
        // driver_vendett2,	/* GX081 (c) 1991 (Asia) */
        /*TODO*/
        // driver_vendettj,	/* GX081 (c) 1991 (Japan) */
        /*TODO*/
        // driver_wecleman,	/* GX602 (c) 1986 */
        /*TODO*/
        // driver_hotchase,	/* GX763 (c) 1988 */
        /*TODO*/
        // TESTDRIVER( chqflag,	/* GX717 (c) 1988 */
        /*TODO*/
        // TESTDRIVER( chqflagj,	/* GX717 (c) 1988 (Japan) */
        /*TODO*/
        // driver_ultraman,	/* GX910 (c) 1991 Banpresto/Bandai */

        /* Konami "Nemesis hardware" games */
        /*TODO*/
        // driver_nemesis,	/* GX456 (c) 1985 */
        /*TODO*/
        // driver_nemesuk,	/* GX456 (c) 1985 */
        /*TODO*/
        // driver_konamigt,	/* GX561 (c) 1985 */
        /*TODO*/
        // driver_salamand,	/* GX587 (c) 1986 */
        /*TODO*/
        // driver_lifefrce,	/* GX587 (c) 1986 */
        /*TODO*/
        // driver_lifefrcj,	/* GX587 (c) 1986 */
        /* GX400 BIOS based games */
        /*TODO*/
        // driver_rf2,		/* GX561 (c) 1985 */
        /*TODO*/
        // driver_twinbee,	/* GX412 (c) 1985 */
        /*TODO*/
        // driver_gradius,	/* GX456 (c) 1985 */
        /*TODO*/
        // driver_gwarrior,	/* GX578 (c) 1985 */

        /* Konami "Twin 16" games */
        /*TODO*/
        // driver_devilw,	/* GX687 (c) 1987 */
        /*TODO*/
        // driver_darkadv,	/* GX687 (c) 1987 */
        /*TODO*/
        // driver_majuu,		/* GX687 (c) 1987 (Japan) */
        /*TODO*/
        // driver_vulcan,	/* GX785 (c) 1988 */
        /*TODO*/
        // driver_gradius2,	/* GX785 (c) 1988 (Japan) */
        /*TODO*/
        // driver_grdius2a,	/* GX785 (c) 1988 (Japan) */
        /*TODO*/
        // driver_grdius2b,	/* GX785 (c) 1988 (Japan) */
        /*TODO*/
        // driver_cuebrick,	/* GX903 (c) 1989 */
        /*TODO*/
        // driver_fround,	/* GX870 (c) 1988 */
        /*TODO*/
        // driver_hpuncher,	/* GX870 (c) 1988 (Japan) */
        /*TODO*/
        // driver_miaj,		/* GX808 (c) 1989 (Japan) */

        /* Konami Gradius III board */
        /*TODO*/
        // driver_gradius3,	/* GX945 (c) 1989 (Japan) */
        /*TODO*/
        // driver_grdius3a,	/* GX945 (c) 1989 (Asia) */

        /* (some) Konami 68000 games */
        /*TODO*/
        // driver_mia,		/* GX808 (c) 1989 */
        /*TODO*/
        // driver_mia2,		/* GX808 (c) 1989 */
        /*TODO*/
        // driver_tmnt,		/* GX963 (c) 1989 (US) */
        /*TODO*/
        // driver_tmht,		/* GX963 (c) 1989 (UK) */
        /*TODO*/
        // driver_tmntj,		/* GX963 (c) 1989 (Japan) */
        /*TODO*/
        // driver_tmht2p,	/* GX963 (c) 1989 (UK) */
        /*TODO*/
        // driver_tmnt2pj,	/* GX963 (c) 1990 (Japan) */
        /*TODO*/
        // driver_tmnt2po,	/* GX963 (c) 1989 (Oceania) */
        /*TODO*/
        // driver_punkshot,	/* GX907 (c) 1990 (US) */
        /*TODO*/
        // driver_punksht2,	/* GX907 (c) 1990 (US) */
        /*TODO*/
        // driver_lgtnfght,	/* GX939 (c) 1990 (US) */
        /*TODO*/
        // driver_trigon,	/* GX939 (c) 1990 (Japan) */
        /*TODO*/
        // driver_blswhstl,	/* GX060 (c) 1991 */
        /*TODO*/
        // driver_detatwin,	/* GX060 (c) 1991 (Japan) */
        /*TODO*/
        // TESTDRIVER( glfgreat,	/* GX061 (c) 1991 */
        /*TODO*/
        // TESTDRIVER( glfgretj,	/* GX061 (c) 1991 (Japan) */
        /*TODO*/
        // driver_tmnt2,		/* GX063 (c) 1991 (US) */
        /*TODO*/
        // driver_tmnt22p,	/* GX063 (c) 1991 (US) */
        /*TODO*/
        // driver_tmnt2a,	/* GX063 (c) 1991 (Asia) */
        /*TODO*/
        // driver_ssriders,	/* GX064 (c) 1991 (World) */
        /*TODO*/
        // driver_ssrdrebd,	/* GX064 (c) 1991 (World) */
        /*TODO*/
        // driver_ssrdrebc,	/* GX064 (c) 1991 (World) */
        /*TODO*/
        // driver_ssrdruda,	/* GX064 (c) 1991 (US) */
        /*TODO*/
        // driver_ssrdruac,	/* GX064 (c) 1991 (US) */
        /*TODO*/
        // driver_ssrdrubc,	/* GX064 (c) 1991 (US) */
        /*TODO*/
        // driver_ssrdrabd,	/* GX064 (c) 1991 (Asia) */
        /*TODO*/
        // driver_ssrdrjbd,	/* GX064 (c) 1991 (Japan) */
        /*TODO*/
        // driver_xmen,		/* GX065 (c) 1992 (US) */
        /*TODO*/
        // driver_xmen6p,	/* GX065 (c) 1992 */
        /*TODO*/
        // driver_xmen2pj,	/* GX065 (c) 1992 (Japan) */
        /*TODO*/
        // driver_thndrx2,	/* GX073 (c) 1991 (Japan) */

        /*
    Konami System GX game list
    1994.03 Racing Force (GX250)
    1994.03 Golfing Greats 2 (GX218)
    1994.04 Gokujou Parodius (GX321)
    1994.07 Taisen Puzzle-dama (GX315)
    1994.12 Soccer Super Stars (GX427)
    1995.04 TwinBee Yahhoo! (GX424)
    1995.08 Dragoon Might (GX417)
    1995.12 Tokimeki Memorial Taisen Puzzle-dama (GX515)
    1996.01 Salamander 2 (GX521)
    1996.02 Sexy Parodius (GX533)
    1996.03 Daisu-Kiss (GX535)
    1996.03 Slam Dunk 2 / Run & Gun 2 (GX505)
    1996.10 Taisen Tokkae-dama (GX615)
    1996.12 Versus Net Soccer (GX627)
    1997.07 Winning Spike (GX705)
    1997.11 Rushing Heroes (GX?. Not released in Japan)
         */
        /* Exidy games */
        driver_sidetrac, /* (c) 1979 */
        driver_targ, /* (c) 1980 */
        driver_spectar, /* (c) 1980 */
        driver_spectar1, /* (c) 1980 */
        driver_venture, /* (c) 1981 */
        driver_venture2, /* (c) 1981 */
        driver_venture4, /* (c) 1981 */
        driver_mtrap, /* (c) 1981 */
        driver_mtrap3, /* (c) 1981 */
        driver_mtrap4, /* (c) 1981 */
        driver_pepper2, /* (c) 1982 */
        driver_hardhat, /* (c) 1982 */
        driver_fax, /* (c) 1983 */
        /*TODO*/
        // driver_circus,	/* no copyright notice [1977?] */
        /*TODO*/
        // driver_robotbwl,	/* no copyright notice */
        /*TODO*/
        // driver_crash,		/* Exidy [1979?] */
        /*TODO*/
        // driver_ripcord,	/* Exidy [1977?] */
        /*TODO*/
        // driver_starfire,	/* Exidy [1979?] */
        /*TODO*/
        // driver_fireone,	/* (c) 1979 Exidy */
        driver_victory, /* (c) 1982 */
        driver_victorba, /* (c) 1982 */
        /* Exidy 440 games */
        /*TODO*/
        // driver_crossbow,	/* (c) 1983 */
        /*TODO*/
        // driver_cheyenne,	/* (c) 1984 */
        /*TODO*/
        // driver_combat,	/* (c) 1985 */
        /*TODO*/
        // driver_cracksht,	/* (c) 1985 */
        /*TODO*/
        // driver_claypign,	/* (c) 1986 */
        /*TODO*/
        // driver_chiller,	/* (c) 1986 */
        /*TODO*/
        // driver_topsecex,	/* (c) 1986 */
        /*TODO*/
        // driver_hitnmiss,	/* (c) 1987 */
        /*TODO*/
        // driver_hitnmis2,	/* (c) 1987 */
        /*TODO*/
        // driver_whodunit,	/* (c) 1988 */
        /*TODO*/
        // driver_showdown,	/* (c) 1988 */

        /* Atari vector games */
        driver_bzone, /* (c) 1980 */
        driver_bzone2, /* (c) 1980 */
        driver_redbaron, /* (c) 1980 */
        driver_tempest, /* (c) 1980 */
        driver_tempest1, /* (c) 1980 */
        driver_tempest2, /* (c) 1980 */
        driver_temptube, /* hack */
        driver_starwars, /* (c) 1983 */
        driver_starwar1, /* (c) 1983 */
        driver_esb, /* (c) 1985 */
        /*TODO*/
        // driver_quantum,	/* (c) 1982 */	/* made by Gencomp */
        /*TODO*/
        // driver_quantum1,	/* (c) 1982 */	/* made by Gencomp */
        /*TODO*/
        // driver_quantump,	/* (c) 1982 */	/* made by Gencomp */

        /* Atari b/w games */
        driver_sprint1, /* no copyright notice */
        driver_sprint2, /* no copyright notice */
        driver_sbrkout, /* no copyright notice */
        driver_dominos, /* no copyright notice */
        driver_nitedrvr, /* no copyright notice [1976] */
        /*TODO*/
        // driver_bsktball,	/* no copyright notice */
        /*TODO*/
        // driver_copsnrob,	/* [1976] */
        /*TODO*/
        // driver_avalnche,	/* no copyright notice [1978] */
        driver_subs, /* no copyright notice [1976] */
        /*TODO*/
        // driver_atarifb,	/* no copyright notice [1978] */
        /*TODO*/
        // driver_atarifb1,	/* no copyright notice [1978] */
        /*TODO*/
        // driver_atarifb4,	/* no copyright notice [1979] */
        /*TODO*/
        // driver_abaseb,	/* no copyright notice [1979] */
        /*TODO*/
        // driver_abaseb2,	/* no copyright notice [1979] */
        /*TODO*/
        // driver_soccer,	/* no copyright notice */
        /*TODO*/
        // driver_canyon,	/* no copyright notice [1977] */
        /*TODO*/
        // driver_canbprot,	/* no copyright notice [1977] */

        driver_videopin, /* no copyright notice [1979] */
        driver_milliped, /* (c) 1982 */
        driver_qwakprot, /* (c) 1982 */
        /* Atari "Missile Command hardware" games */
        /* misc Atari games */
        /*TODO*/
        // driver_foodf,		/* (c) 1982 */	/* made by Gencomp */
        /*TODO*/
        // driver_liberatr,	/* (c) 1982 */
        /*TODO*/
        // TESTDRIVER( liberat2,

        driver_cloak,		/* (c) 1983 */
        driver_cloud9,	/* (c) 1983 */
        driver_jedi, /* (c) 1984 */
        /* Atari System 1 games */
        /*TODO*/
        // driver_marble,	/* (c) 1984 */
        /*TODO*/
        // driver_marble2,	/* (c) 1984 */
        /*TODO*/
        // driver_marblea,	/* (c) 1984 */
        /*TODO*/
        // driver_peterpak,	/* (c) 1984 */
        /*TODO*/
        // driver_indytemp,	/* (c) 1985 */
        /*TODO*/
        // driver_indytem2,	/* (c) 1985 */
        /*TODO*/
        // driver_indytem3,	/* (c) 1985 */
        /*TODO*/
        // driver_indytem4,	/* (c) 1985 */
        /*TODO*/
        // driver_roadrunn,	/* (c) 1985 */
        /*TODO*/
        // driver_roadblst,	/* (c) 1986, 1987 */

        /* Atari System 2 games */
        /*TODO*/
        // driver_paperboy,	/* (c) 1984 */
        /*TODO*/
        // driver_ssprint,	/* (c) 1986 */
        /*TODO*/
        // driver_csprint,	/* (c) 1986 */
        /*TODO*/
        // driver_720,		/* (c) 1986 */
        /*TODO*/
        // driver_720b,		/* (c) 1986 */
        /*TODO*/
        // driver_apb,		/* (c) 1987 */
        /*TODO*/
        // driver_apb2,		/* (c) 1987 */

        /* later Atari games */
        /*TODO*/
        // driver_gauntlet,	/* (c) 1985 */
        /*TODO*/
        // driver_gauntir1,	/* (c) 1985 */
        /*TODO*/
        // driver_gauntir2,	/* (c) 1985 */
        /*TODO*/
        // driver_gaunt2p,	/* (c) 1985 */
        /*TODO*/
        // driver_gaunt2,	/* (c) 1986 */
        /*TODO*/
        // driver_vindctr2,	/* (c) 1988 */

        /*TODO*/
        // driver_toobin,	/* (c) 1988 */
        /*TODO*/
        // driver_toobin2,	/* (c) 1988 */
        /*TODO*/
        // driver_toobinp,	/* (c) 1988 */
        /*TODO*/
        // driver_vindictr,	/* (c) 1988 */
        /*TODO*/
        // driver_klax,		/* (c) 1989 */
        /*TODO*/
        // driver_klax2,		/* (c) 1989 */
        /*TODO*/
        // driver_klax3,		/* (c) 1989 */
        /*TODO*/
        // driver_klaxj,		/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_blstroid,	/* (c) 1987 */
        /*TODO*/
        // driver_blstroi2,	/* (c) 1987 */
        /*TODO*/
        // driver_blsthead,	/* (c) 1987 */
        /*TODO*/
        // driver_xybots,	/* (c) 1987 */
        /*TODO*/
        // driver_eprom,		/* (c) 1989 */
        /*TODO*/
        // driver_eprom2,	/* (c) 1989 */
        /*TODO*/
        // driver_skullxbo,	/* (c) 1989 */
        /*TODO*/
        // driver_skullxb2,	/* (c) 1989 */
        /*TODO*/
        // driver_badlands,	/* (c) 1989 */
        /*TODO*/
        // driver_cyberbal,	/* (c) 1989 */
        /*TODO*/
        // driver_cyberba2,	/* (c) 1989 */
        /*TODO*/
        // driver_cyberbt,	/* (c) 1989 */
        /*TODO*/
        // driver_cyberb2p,	/* (c) 1989 */
        /*TODO*/
        // driver_rampart,	/* (c) 1990 */
        /*TODO*/
        // driver_ramprt2p,	/* (c) 1990 */
        /*TODO*/
        // driver_rampartj,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_shuuz,		/* (c) 1990 */
        /*TODO*/
        // driver_shuuz2,	/* (c) 1990 */
        /*TODO*/
        // driver_hydra,		/* (c) 1990 */
        /*TODO*/
        // driver_hydrap,	/* (c) 1990 */
        /*TODO*/
        // driver_pitfight,	/* (c) 1990 */
        /*TODO*/
        // driver_pitfigh3,	/* (c) 1990 */
        /*TODO*/
        // driver_thunderj,	/* (c) 1990 */
        /*TODO*/
        // driver_batman,	/* (c) 1991 */
        /*TODO*/
        // driver_relief,	/* (c) 1992 */
        /*TODO*/
        // driver_relief2,	/* (c) 1992 */
        /*TODO*/
        // driver_offtwall,	/* (c) 1991 */
        /*TODO*/
        // driver_offtwalc,	/* (c) 1991 */
        /*TODO*/
        // driver_arcadecl,	/* (c) 1992 */
        /*TODO*/
        // driver_sparkz,	/* (c) 1992 */

        /* SNK / Rock-ola games */
        driver_sasuke, /* [1980] Shin Nihon Kikaku (SNK) */
        driver_satansat, /* (c) 1981 SNK */
        driver_zarzon, /* (c) 1981 Taito, gameplay says SNK */
        driver_vanguard, /* (c) 1981 SNK */
        driver_vangrdce, /* (c) 1981 SNK + Centuri */
        driver_fantasy, /* (c) 1981 Rock-ola */
        driver_fantasyj, /* (c) 1981 SNK */
        driver_pballoon, /* (c) 1982 SNK */
        driver_nibbler, /* (c) 1982 Rock-ola */
        driver_nibblera, /* (c) 1982 Rock-ola */
        /* later SNK games, each game can be identified by PCB code and ROM
    code, the ROM code is the same between versions, and usually based
    upon the Japanese title. */
        driver_lasso,		/*       'WM' (c) 1982 */	
        /*TODO*/
        // driver_ikari3,	/* A7007 'IK3'(c) 1989 */
        /*TODO*/
        // driver_pow,		/* A7008 'DG' (c) 1988 */
        /*TODO*/
        // driver_powj,		/* A7008 'DG' (c) 1988 */
        /*TODO*/
        // driver_searchar,	/* A8007 'BH' (c) 1989 */
        /*TODO*/
        // driver_sercharu,	/* A8007 'BH' (c) 1989 */
        /*TODO*/
        // driver_streetsm,	/* A8007 'S2' (c) 1989 */
        /*TODO*/
        // driver_streets1,	/* A7008 'S2' (c) 1989 */
        /*TODO*/
        // driver_streetsj,	/* A8007 'S2' (c) 1989 */
        /* Mechanized Attack   A8002 'MA' (c) 1989 */
        /*TODO*/
        // driver_prehisle,	/* A8003 'GT' (c) 1989 */
        /*TODO*/
        // driver_prehislu,	/* A8003 'GT' (c) 1989 */
        /*TODO*/
        // driver_gensitou,	/* A8003 'GT' (c) 1989 */
        /* Beast Busters       A9003 'BB' (c) 1989 */
        /* SNK / Alpha 68K games */
        /*TODO*/
        // TESTDRIVER( kouyakyu,
        /*TODO*/
        // driver_sstingry,	/* (c) 1986 Alpha Denshi Co. */
        /*TODO*/
        // driver_kyros,		/* (c) 1987 World Games */
        /*TODO*/
        // TESTDRIVER( paddlema,	/* Alpha-68K96I  'PM' (c) 1988 SNK */
        /*TODO*/
        // driver_timesold,	/* Alpha-68K96II 'BT' (c) 1987 SNK / Romstar */
        /*TODO*/
        // driver_timesol1,  /* Alpha-68K96II 'BT' (c) 1987 */
        /*TODO*/
        // driver_btlfield,  /* Alpha-68K96II 'BT' (c) 1987 */
        /*TODO*/
        // driver_skysoldr,	/* Alpha-68K96II 'SS' (c) 1988 SNK (Romstar with dip switch) */
        /*TODO*/
        // driver_goldmedl,	/* Alpha-68K96II 'GM' (c) 1988 SNK */
        /*TODO*/
        // TESTDRIVER( goldmedb,	/* Alpha-68K96II bootleg */
        /*TODO*/
        // driver_skyadvnt,	/* Alpha-68K96V  'SA' (c) 1989 SNK of America licensed from Alpha */
        /*TODO*/
        // driver_gangwars,	/* Alpha-68K96V       (c) 1989 Alpha Denshi Co. */
        /*TODO*/
        // driver_gangwarb,	/* Alpha-68K96V bootleg */
        /*TODO*/
        // driver_sbasebal,	/* Alpha-68K96V       (c) 1989 SNK of America licensed from Alpha */

        /*TODO*/
        // driver_ctribe,	/* TA-0028 (c) 1990 (US) */
        /*TODO*/
        // driver_ctribeb,	/* bootleg */
        /*TODO*/
        // driver_blockout,	/* TA-0029 (c) 1989 + California Dreams */
        /*TODO*/
        // driver_blckout2,	/* TA-0029 (c) 1989 + California Dreams */
        /*TODO*/
        // driver_ddragon3,	/* TA-0030 (c) 1990 */
        /*TODO*/
        // driver_ddrago3b,	/* bootleg */
        /* TA-0031 WWF Wrestlefest */
        /* Zaccaria games */
        driver_sia2650,
        driver_tinv2650,
        /* UPL games */
        driver_mnight, /* (c) 1987 distributed by Kawakus */
        driver_arkarea, /* UPL-87007 (c) [1988?] */
        /*
    Urashima Mahjong    UPL-89052

    UPL Game List
    V1.2   May 27,1999

       83 Mouser                              Kit 2P              Action   83001
     3/84 Nova 2001                 Universal Kit 2P  8W+2B   HC  Shooter  85005
       84 Penguin Wars (Kun)                      2P              Action
       84 Ninja Kun                 Taito                                  85003
       85 Raiders 5                 Taito                                  85004
     8/87 Mission XX                          Kit 2P  8W+2B   VC  Shooter  86001
       87 Mutant Night                        Kit 2P  8W+2B   HC  Action
     7/87 Rad Action/Ninja Taro   World Games Kit 2P  8W+2B   HC  Action   87003
     7/87 Ninja Taro/Rad Action   World Games Kit 2P  8W+2B   HC  Action
       87 Ninja Taro II                       Kit 2P  8W+2B   HC  Action
       88 Aquaria                             Kit 2P  8W+2B
       89 Ochichi Mahjong                     Kit 2P  8W+2B   HC  Mahjong
     9/89 Omega Fighter        American Sammy Kit 2P  8W+2B   HC  Shooter  89016
    12/89 Task Force Harrier   American Sammy Kit 2P  8W+2B   VC  Shooter  89053
       90 Atomic Robo-Kid      American Sammy Kit 2P  8W+2B   HC  Shooter  88013
       90 Mustang - U.S.A.A.F./Fire Mustang   Kit 2P  8W+2B   HC  Shooter  90058
       91 Acrobat Mission               Taito Kit 2P  8W+2B   VC  Shooter
       91 Bio Ship Paladin/Spaceship Gomera   Kit 2P  8W+2B   HC  Shooter  90062
       91 Black Heart                         Kit 2P  8W+2B   HC  Shooter
       91 Van Dyke Fantasy                    Kit 2P  8W+2B
     2/92 Strahl                              Kit 2P  8W+3B                91074
          Thunder Dragon 2                                                 93091

         */
        /* Williams/Midway TMS34010 games */
        /*TODO*/
        // driver_narc,		/* (c) 1988 Williams */
        /*TODO*/
        // driver_narc3,		/* (c) 1988 Williams */
        /*TODO*/
        // driver_trog,		/* (c) 1990 Midway */
        /*TODO*/
        // driver_trog3,		/* (c) 1990 Midway */
        /*TODO*/
        // driver_trogp,		/* (c) 1990 Midway */
        /*TODO*/
        // driver_smashtv,	/* (c) 1990 Williams */
        /*TODO*/
        // driver_smashtv6,	/* (c) 1990 Williams */
        /*TODO*/
        // driver_smashtv5,	/* (c) 1990 Williams */
        /*TODO*/
        // driver_smashtv4,	/* (c) 1990 Williams */
        /*TODO*/
        // driver_hiimpact,	/* (c) 1990 Williams */
        /*TODO*/
        // driver_shimpact,	/* (c) 1991 Midway */
        /*TODO*/
        // driver_strkforc,	/* (c) 1991 Midway */
        /*TODO*/
        // driver_mk,		/* (c) 1992 Midway */
        /*TODO*/
        // driver_mkla1,		/* (c) 1992 Midway */
        /*TODO*/
        // driver_mkla2,		/* (c) 1992 Midway */
        /*TODO*/
        // driver_mkla3,		/* (c) 1992 Midway */
        /*TODO*/
        // driver_mkla4,		/* (c) 1992 Midway */
        /*TODO*/
        // driver_term2,		/* (c) 1992 Midway */
        /*TODO*/
        // driver_totcarn,	/* (c) 1992 Midway */
        /*TODO*/
        // driver_totcarnp,	/* (c) 1992 Midway */
        /*TODO*/
        // driver_mk2,		/* (c) 1993 Midway */
        /*TODO*/
        // driver_mk2r32,	/* (c) 1993 Midway */
        /*TODO*/
        // driver_mk2r14,	/* (c) 1993 Midway */
        /*TODO*/
        // driver_nbajam,	/* (c) 1993 Midway */
        /*TODO*/
        // driver_nbajamr2,	/* (c) 1993 Midway */
        /*TODO*/
        // driver_nbajamte,	/* (c) 1994 Midway */
        /*TODO*/
        // driver_nbajamt1,	/* (c) 1994 Midway */
        /*TODO*/
        // driver_nbajamt2,	/* (c) 1994 Midway */
        /*TODO*/
        // driver_nbajamt3,	/* (c) 1994 Midway */
        /*TODO*/
        // driver_mk3,		/* (c) 1994 Midway */
        /*TODO*/
        // driver_mk3r20,	/* (c) 1994 Midway */
        /*TODO*/
        // driver_mk3r10,	/* (c) 1994 Midway */
        /*TODO*/
        // driver_umk3,		/* (c) 1994 Midway */
        /*TODO*/
        // driver_umk3r11,	/* (c) 1994 Midway */
        /*TODO*/
        // driver_wwfmania,	/* (c) 1995 Midway */
        /*TODO*/
        // driver_openice,	/* (c) 1995 Midway */
        /*TODO*/
        // driver_nbamaxht,	/* (c) 1996 Midway */
        /*TODO*/
        // driver_rmpgwt,	/* (c) 1997 Midway */
        /*TODO*/
        // driver_rmpgwt11,	/* (c) 1997 Midway */

        /* Cinematronics vector games */
        /*TODO*/
        // driver_spacewar,
        /*TODO*/
        // driver_barrier,
        /*TODO*/
        // driver_starcas,	/* (c) 1980 */
        /*TODO*/
        // driver_starcas1,	/* (c) 1980 */
        /*TODO*/
        // driver_tailg,
        /*TODO*/
        // driver_ripoff,
        /*TODO*/
        // driver_armora,
        /*TODO*/
        // driver_wotw,
        /*TODO*/
        // driver_warrior,
        /*TODO*/
        // driver_starhawk,
        /*TODO*/
        // driver_solarq,	/* (c) 1981 */
        /*TODO*/
        // driver_boxingb,	/* (c) 1981 */
        /*TODO*/
        // driver_speedfrk,
        /*TODO*/
        // driver_sundance,
        /*TODO*/
        // driver_demon,		/* (c) 1982 Rock-ola */
        /* this one uses 68000+Z80 instead of the Cinematronics CPU */
        /*TODO*/
        // driver_cchasm,
        /*TODO*/
        // driver_cchasm1,	/* (c) 1983 Cinematronics / GCE */

        /* Seibu Denshi / Seibu Kaihatsu games */
        /*TODO*/
        // driver_kncljoe,	/* (c) 1985 Taito Corporation */
        /*TODO*/
        // driver_kncljoea,	/* (c) 1985 Taito Corporation */

        /*TODO*/
        // driver_dynduke,	/* (c) 1989 Seibu Kaihatsu + Fabtek license */
        /*TODO*/
        // driver_dbldyn,	/* (c) 1989 Seibu Kaihatsu + Fabtek license */
        /*TODO*/
        // driver_raiden,	/* (c) 1990 Seibu Kaihatsu */
        /*TODO*/
        // driver_raidena,	/* (c) 1990 Seibu Kaihatsu */
        /*TODO*/
        // driver_raidenk,	/* (c) 1990 Seibu Kaihatsu + IBL Corporation license */
        /*TODO*/
        // driver_dcon,		/* (c) 1992 Success */

        /* Seibu STI System games:

    Viper: Phase 1 					(c) 1995
    Viper: Phase 1 (New version)	(c) 1996
    Battle Balls					(c) 1996
    Raiden Fighters					(c) 1996
    Raiden Fighters 2 				(c) 1997
    Senku							(c) 1997

         */
        /* Tad games (Tad games run on Seibu hardware) */
        /*TODO*/
        // driver_cabal,		/* (c) 1988 Tad + Fabtek license */
        /*TODO*/
        // driver_cabal2,	/* (c) 1988 Tad + Fabtek license */
        /*TODO*/
        // driver_cabalbl,	/* bootleg */
        /*TODO*/
        // driver_toki,		/* (c) 1989 Tad */
        /*TODO*/
        // driver_toki2,		/* (c) 1989 Tad */
        /*TODO*/
        // driver_toki3,		/* (c) 1989 Tad */
        /*TODO*/
        // driver_tokiu,		/* (c) 1989 Tad + Fabtek license */
        /*TODO*/
        // driver_tokib,		/* bootleg */
        /*TODO*/
        // driver_bloodbro,	/* (c) 1990 Tad */
        /*TODO*/
        // driver_weststry,	/* bootleg */

        /* Jaleco games */
        driver_exerion, /* (c) 1983 Jaleco */
        driver_exeriont, /* (c) 1983 Jaleco + Taito America license */
        driver_exerionb, /* bootleg */
        /*TODO*/
        // driver_formatz,	/* (c) 1984 Jaleco */
        /*TODO*/
        // driver_aeroboto,	/* (c) 1984 Williams */

        driver_pinbo, /* (c) 1984 Jaleco */
        driver_pinbos, /* (c) 1985 Strike */
        driver_psychic5, /* (c) 1987 Jaleco */
        /*TODO*/
        // driver_ginganin,	/* (c) 1987 Jaleco */
        /*TODO*/
        // driver_cischeat,	/* (c) 1990 Jaleco */
        /*TODO*/
        // driver_f1gpstar,	/* (c) 1991 Jaleco */

        /* Jaleco Mega System 1 games */
        /*TODO*/
        // driver_lomakai,	/* (c) 1988 (World) */
        /*TODO*/
        // driver_makaiden,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_p47,		/* (c) 1988 */
        /*TODO*/
        // driver_p47j,		/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_kickoff,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_tshingen,	/* (c) 1988 (Japan) */
        /*TODO*/
        // driver_astyanax,	/* (c) 1989 */
        /*TODO*/
        // driver_lordofk,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_hachoo,	/* (c) 1989 */
        /*TODO*/
        // driver_plusalph,	/* (c) 1989 */
        /*TODO*/
        // driver_stdragon,	/* (c) 1989 */
        /*TODO*/
        // driver_iganinju,	/* (c) 1989 (Japan) */
        /*TODO*/
        // driver_rodland,	/* (c) 1990 */
        /*TODO*/
        // driver_rodlandj,	/* (c) 1990 (Japan) */
        /*TODO*/
        // driver_64street,	/* (c) 1991 */
        /*TODO*/
        // driver_64streej,	/* (c) 1991 (Japan) */
        /*TODO*/
        // driver_edf,		/* (c) 1991 */
        /*TODO*/
        // driver_avspirit,	/* (c) 1991 */
        /*TODO*/
        // driver_phantasm,	/* (c) 1991 (Japan) */
        /*TODO*/
        // driver_bigstrik,	/* (c) 1992 */
        /*TODO*/
        // driver_chimerab,	/* (c) 1993 */
        /*TODO*/
        // driver_cybattlr,	/* (c) 1993 */
        /*TODO*/
        // driver_peekaboo,	/* (c) 1993 */
        /*TODO*/
        // driver_soldamj,	/* (c) 1992 (Japan) */

        /* Video System Co. games */
        /*TODO*/
        // driver_rabiolep,	/* (c) 1987 V-System Co. (Japan) */
        /*TODO*/
        // driver_rpunch,	/* (c) 1987 V-System Co. + Bally/Midway/Sente license (US) */
        /*TODO*/
        // driver_svolley,	/* (c) 1989 V-System Co. (Japan) */
        /*TODO*/
        // driver_svolleyk,	/* (c) 1989 V-System Co. (Japan) */
        /*TODO*/
        // driver_tail2nos,	/* [1989] V-System Co. */
        /*TODO*/
        // driver_sformula,	/* [1989] V-System Co. (Japan) */

        /*TODO*/
        // driver_pspikes,	/* (c) 1991 Video System Co. (Korea) */
        /*TODO*/
        // driver_svolly91,	/* (c) 1991 Video System Co. */
        /*TODO*/
        // driver_karatblz,	/* (c) 1991 Video System Co. */
        /*TODO*/
        // driver_karatblu,	/* (c) 1991 Video System Co. (US) */
        /*TODO*/
        // driver_spinlbrk,	/* (c) 1990 V-System Co. (World) */
        /*TODO*/
        // driver_spinlbru,	/* (c) 1990 V-System Co. (US) */
        /*TODO*/
        // driver_spinlbrj,	/* (c) 1990 V-System Co. (Japan) */
        /*TODO*/
        // driver_turbofrc,	/* (c) 1991 Video System Co. */
        /*TODO*/
        // driver_aerofgt,	/* (c) 1992 Video System Co. */
        /*TODO*/
        // driver_aerofgtb,	/* (c) 1992 Video System Co. */
        /*TODO*/
        // driver_aerofgtc,	/* (c) 1992 Video System Co. */
        /*TODO*/
        // driver_sonicwi,	/* (c) 1992 Video System Co. (Japan) */

        /* Psikyo games */
        /*TODO*/
        // driver_sngkace,	/* (c) 1993 */
        /*TODO*/
        // driver_gunbird,	/* (c) 1994 */
        /*TODO*/
        // TESTDRIVER( s1945,		/* (c) 1995 */
        /*TODO*/
        // TESTDRIVER( sngkblad,	/* (c) 1996 */

        /* Vastar was made by Orca, but when it was finished, Orca had already bankrupted. */
        /* So they sold this game as "Made by Sesame Japan" because they couldn't use */
        /* the name "Orca" */
        driver_vastar, /* (c) 1983 Sesame Japan */
        driver_vastar2, /* (c) 1983 Sesame Japan */
        /*
    other Orca games:
    82 Battle Cross                         Kit 2P
    82 River Patrol Empire Mfg/Kerstens Ind Ded 2P        HC Action
    82 Slalom                               Kit 2P        HC Action
    83 Net Wars                                 2P
    83 Super Crush                          Kit 2P           Action
         */
        /* Gaelco games */
        /*TODO*/
        // driver_bigkarnk,	/* (c) 1991 Gaelco */
        /*TODO*/
        // driver_splash,	/* (c) 1992 Gaelco */
        /*TODO*/
        // driver_biomtoy,	/* (c) 1995 Gaelco */
        /*TODO*/
        // TESTDRIVER( maniacsq,	/* (c) 1996 Gaelco */

        /*
    Gaelco Game list:
    =================

    1987:	Master Boy
    1991:	Big Karnak, Master Boy 2
    1992:	Splash, Thunder Hoop, Squash
    1993:	World Rally, Glass
    1994:	Strike Back, Target Hits, Thunder Hoop 2
    1995:	Alligator Hunt, Toy, World Rally 2, Salter, Touch & Go
    1996:	Maniac Square, Snow Board, Speed Up
    1997:	Surf Planet
    1998:	Radikal Bikers
    1999:	Rolling Extreme

    All games newer than Splash are heavily protected.
         */
        /* Kaneko "AX System" games */
        /*TODO*/
        // driver_berlwall,	/* (c) 1991 Kaneko */
        /*TODO*/
        // driver_berlwalt,	/* (c) 1991 Kaneko */
        /*TODO*/
        // driver_gtmr,		/* (c) 1994 Kaneko */
        /*TODO*/
        // driver_gtmre,		/* (c) 1994 Kaneko */
        /*TODO*/
        // TESTDRIVER( gtmr2,
        /*TODO*/
        // TESTDRIVER( shogwarr,

        /* other Kaneko games */
        /*TODO*/
        // driver_galpanic,	/* (c) 1990 Kaneko */

        /* Seta games */
        /*TODO*/
        // driver_tndrcade,	/* UA-0 (c) 1987 Taito */
        /*TODO*/
        // driver_tndrcadj,	/* UA-0 (c) 1987 Taito */
        /*TODO*/
        // driver_twineagl,	/* UA-2 (c) 1988 + Taito license */
        /*TODO*/
        // driver_downtown,	/* UD-2 (c) 1989 + Romstar or Taito license (DSW) */
        /*TODO*/
        // driver_usclssic,	/* UE   (c) 1989 + Romstar or Taito license (DSW) */
        /*TODO*/
        // driver_calibr50,	/* UH   (c) 1989 + Romstar or Taito license (DSW) */
        /*TODO*/
        // driver_drgnunit,	/* (c) 1989 Athena / Seta + Romstar or Taito license (DSW) */
        /*TODO*/
        // driver_arbalest,	/* UK   (c) 1989 + Jordan, Romstar or Taito license (DSW) */
        /*TODO*/
        // driver_metafox,	/* UP   (c) 1989 + Jordan, Romstar or Taito license (DSW) */
        /*TODO*/
        // driver_blandia,	/* (c) 1992 Allumer */
        /*TODO*/
        // driver_zingzip,	/* UY   (c) 1992 Allumer + Tecmo */
        /*TODO*/
        // TESTDRIVER( msgundam,
        /*TODO*/
        // driver_wrofaero,	/* (c) 1993 Yang Cheng */

        /* Atlus games */
        /*TODO*/
        // driver_ohmygod,	/* (c) 1993 Atlus (Japan) */
        /*TODO*/
        // driver_powerins,	/* (c) 1993 Atlus (Japan) */

        /* SunSoft games */
        /*TODO*/
        // driver_shanghai,	/* (c) 1988 Sunsoft (Sun Electronics) */
        /*TODO*/
        // driver_shangha3,	/* (c) 1993 Sunsoft */
        /*TODO*/
        // driver_heberpop,	/* (c) 1994 Sunsoft / Atlus */
        /*TODO*/
        // driver_blocken,	/* (c) 1994 KID / Visco */

        /* Suna games */
        /*TODO*/
        // driver_bssoccer,	/* (c) 1996 Suna */

        driver_rshark,//68000k game TODO

        /* NMK games */
        /*TODO*/
        /// *TODO*///TESTDRIVER( macross,	/* (c) 1992 NMK + Big West */
        /*TODO*/
        // driver_bjtwin,	/* (c) 1993 NMK */

        /* Comad games */
        /*TODO*/
        // driver_zerozone,	/* (c) 1993 Comad */
        /*TODO*/
        // driver_hotpinbl,	/* (c) 1995 Comad & New Japan System */
        /*TODO*/
        // driver_galspnbl,	/* (c) 1996 Comad */
        /*TODO*/
        /// *TODO*///TESTDRIVER( ladyfrog,
        /* the following ones run on modified Gals Panic hardware */
        /*TODO*/
        // driver_fantasia,	/* (c) 1994 Comad & New Japan System */
        /*TODO*/
        // driver_newfant,	/* (c) 1995 Comad & New Japan System */
        /*TODO*/
        // driver_missw96,	/* (c) 1996 Comad */

        /* Playmark games */
        /*TODO*/
        // driver_bigtwin,	/* (c) 1995 */
        /*TODO*/
        // driver_wbeachvl,	/* (c) 1995 */        
        /*TODO*/
        // driver_omegrace,	/* (c) 1981 Midway */

        driver_leprechn, /* (c) 1982 Tong Electronic */
        driver_potogold, /* (c) 1982 Tong Electronic */
        driver_irobot, /* (c) 1983 Atari */
        /*TODO*/
        // driver_spiders,	/* (c) 1981 Sigma Ent. Inc. */
        /*TODO*/
        // driver_spiders2,	/* (c) 1981 Sigma Ent. Inc. */
        driver_stactics,	/* [1981 Sega] */
        /*TODO*/
        // driver_exterm,	/* (c) 1989 Premier Technology - a Gottlieb game */

        driver_starcrus,	/* [1977 Ramtek] */

        /*TODO*/
        /// *TODO*///TESTDRIVER( dlair,
        driver_meteor,	/* (c) 1981 Venture Line */
        /*TODO*/
        // driver_aztarac,	/* (c) 1983 Centuri (vector game) */
        driver_mole, /* (c) 1982 Yachiyo Electronics, Ltd. */
        /* Neo Geo games */
        /* the four digits number is the game ID stored at address 0x0108 of the program ROM */
        /*TODO*/
        // driver_nam1975,	/* 0001 (c) 1990 SNK */
        /*TODO*/
        // driver_bstars,	/* 0002 (c) 1990 SNK */
        /*TODO*/
        // driver_tpgolf,	/* 0003 (c) 1990 SNK */
        /*TODO*/
        // driver_mahretsu,	/* 0004 (c) 1990 SNK */
        /*TODO*/
        // driver_maglord,	/* 0005 (c) 1990 Alpha Denshi Co. */
        /*TODO*/
        // driver_maglordh,	/* 0005 (c) 1990 Alpha Denshi Co. */
        /*TODO*/
        // driver_ridhero,	/* 0006 (c) 1990 SNK */
        /*TODO*/
        // driver_alpham2,	/* 0007 (c) 1991 SNK */
        /* 0008 */
        /*TODO*/
        // driver_ncombat,	/* 0009 (c) 1990 Alpha Denshi Co. */
        /*TODO*/
        // driver_cyberlip,	/* 0010 (c) 1990 SNK */
        /*TODO*/
        // driver_superspy,	/* 0011 (c) 1990 SNK */
        /* 0012 */
        /* 0013 */
        /*TODO*/
        // driver_mutnat,	/* 0014 (c) 1992 SNK */
        /* 0015 */
        /*TODO*/
        // driver_kotm,		/* 0016 (c) 1991 SNK */
        /*TODO*/
        // driver_sengoku,	/* 0017 (c) 1991 SNK */
        /*TODO*/
        // driver_sengokh,	/* 0017 (c) 1991 SNK */
        /*TODO*/
        // driver_burningf,	/* 0018 (c) 1991 SNK */
        /*TODO*/
        // driver_burningh,	/* 0018 (c) 1991 SNK */
        /*TODO*/
        // driver_lbowling,	/* 0019 (c) 1990 SNK */
        /*TODO*/
        // driver_gpilots,	/* 0020 (c) 1991 SNK */
        /*TODO*/
        // driver_joyjoy,	/* 0021 (c) 1990 SNK */
        /*TODO*/
        // driver_bjourney,	/* 0022 (c) 1990 Alpha Denshi Co. */
        /*TODO*/
        // driver_quizdais,	/* 0023 (c) 1991 SNK */
        /*TODO*/
        // driver_lresort,	/* 0024 (c) 1992 SNK */
        /*TODO*/
        // driver_eightman,	/* 0025 (c) 1991 SNK / Pallas */
        /* 0026 Fun Fun Brothers - prototype? */
        /*TODO*/
        // driver_minasan,	/* 0027 (c) 1990 Monolith Corp. */
        /* 0028 */
        /*TODO*/
        // driver_legendos,	/* 0029 (c) 1991 SNK */
        /*TODO*/
        // driver_2020bb,	/* 0030 (c) 1991 SNK / Pallas */
        /*TODO*/
        // driver_2020bbh,	/* 0030 (c) 1991 SNK / Pallas */
        /*TODO*/
        // driver_socbrawl,	/* 0031 (c) 1991 SNK */
        /*TODO*/
        // driver_roboarmy,	/* 0032 (c) 1991 SNK */
        /*TODO*/
        // driver_fatfury1,	/* 0033 (c) 1991 SNK */
        /*TODO*/
        // driver_fbfrenzy,	/* 0034 (c) 1992 SNK */
        /* 0035 */
        /*TODO*/
        // driver_bakatono,	/* 0036 (c) 1991 Monolith Corp. */
        /*TODO*/
        // driver_crsword,	/* 0037 (c) 1991 Alpha Denshi Co. */
        /*TODO*/
        // driver_trally,	/* 0038 (c) 1991 Alpha Denshi Co. */
        /*TODO*/
        // driver_kotm2,		/* 0039 (c) 1992 SNK */
        /*TODO*/
        // driver_sengoku2,	/* 0040 (c) 1993 SNK */
        /*TODO*/
        // driver_bstars2,	/* 0041 (c) 1992 SNK */
        /*TODO*/
        // driver_quizdai2,	/* 0042 (c) 1992 SNK */
        /*TODO*/
        // driver_3countb,	/* 0043 (c) 1993 SNK */
        /*TODO*/
        // driver_aof,		/* 0044 (c) 1992 SNK */
        /*TODO*/
        // driver_samsho,	/* 0045 (c) 1993 SNK */
        /*TODO*/
        // driver_tophuntr,	/* 0046 (c) 1994 SNK */
        /*TODO*/
        // driver_fatfury2,	/* 0047 (c) 1992 SNK */
        /*TODO*/
        // driver_janshin,	/* 0048 (c) 1994 Aicom */
        /*TODO*/
        // driver_androdun,	/* 0049 (c) 1992 Visco */
        /*TODO*/
        // driver_ncommand,	/* 0050 (c) 1992 Alpha Denshi Co. */
        /*TODO*/
        // driver_viewpoin,	/* 0051 (c) 1992 Sammy */
        /*TODO*/
        // driver_ssideki,	/* 0052 (c) 1992 SNK */
        /*TODO*/
        // driver_wh1,		/* 0053 (c) 1992 Alpha Denshi Co. */
        /* 0054 Crossed Swords 2 (CD only) */
        /*TODO*/
        // driver_kof94,		/* 0055 (c) 1994 SNK */
        /*TODO*/
        // driver_aof2,		/* 0056 (c) 1994 SNK */
        /*TODO*/
        // driver_wh2,		/* 0057 (c) 1993 ADK */
        /*TODO*/
        // driver_fatfursp,	/* 0058 (c) 1993 SNK */
        /*TODO*/
        // driver_savagere,	/* 0059 (c) 1995 SNK */
        /*TODO*/
        // driver_fightfev,	/* 0060 (c) 1994 Viccom */
        /*TODO*/
        // driver_ssideki2,	/* 0061 (c) 1994 SNK */
        /*TODO*/
        // driver_spinmast,	/* 0062 (c) 1993 Data East Corporation */
        /*TODO*/
        // driver_samsho2,	/* 0063 (c) 1994 SNK */
        /*TODO*/
        // driver_wh2j,		/* 0064 (c) 1994 ADK / SNK */
        /*TODO*/
        // driver_wjammers,	/* 0065 (c) 1994 Data East Corporation */
        /*TODO*/
        // driver_karnovr,	/* 0066 (c) 1994 Data East Corporation */
        /*TODO*/
        // driver_gururin,	/* 0067 (c) 1994 Face */
        /*TODO*/
        // driver_pspikes2,	/* 0068 (c) 1994 Video System Co. */
        /*TODO*/
        // driver_fatfury3,	/* 0069 (c) 1995 SNK */
        /* 0070 */
        /* 0071 */
        /* 0072 */
        /*TODO*/
        // driver_panicbom,	/* 0073 (c) 1994 Eighting / Hudson */
        /*TODO*/
        // driver_aodk,		/* 0074 (c) 1994 ADK / SNK */
        /*TODO*/
        // driver_sonicwi2,	/* 0075 (c) 1994 Video System Co. */
        /*TODO*/
        // driver_zedblade,	/* 0076 (c) 1994 NMK */
        /* 0077 */
        /*TODO*/
        // driver_galaxyfg,	/* 0078 (c) 1995 Sunsoft */
        /*TODO*/
        // driver_strhoop,	/* 0079 (c) 1994 Data East Corporation */
        /*TODO*/
        // driver_quizkof,	/* 0080 (c) 1995 Saurus */
        /*TODO*/
        // driver_ssideki3,	/* 0081 (c) 1995 SNK */
        /*TODO*/
        // driver_doubledr,	/* 0082 (c) 1995 Technos */
        /*TODO*/
        // driver_pbobble,	/* 0083 (c) 1994 Taito */
        /*TODO*/
        // driver_kof95,		/* 0084 (c) 1995 SNK */
        /* 0085 Shinsetsu Samurai Spirits Bushidoretsuden / Samurai Shodown RPG (CD only) */
        /*TODO*/
        // driver_tws96,		/* 0086 (c) 1996 Tecmo */
        /*TODO*/
        // driver_samsho3,	/* 0087 (c) 1995 SNK */
        /*TODO*/
        // driver_stakwin,	/* 0088 (c) 1995 Saurus */
        /*TODO*/
        // driver_pulstar,	/* 0089 (c) 1995 Aicom */
        /*TODO*/
        // driver_whp,		/* 0090 (c) 1995 ADK / SNK */
        /* 0091 */
        /*TODO*/
        // driver_kabukikl,	/* 0092 (c) 1995 Hudson */
        /*TODO*/
        // driver_neobombe,	/* 0093 (c) 1997 Hudson */
        /*TODO*/
        // driver_gowcaizr,	/* 0094 (c) 1995 Technos */
        /*TODO*/
        // driver_rbff1,		/* 0095 (c) 1995 SNK */
        /*TODO*/
        // driver_aof3,		/* 0096 (c) 1996 SNK */
        /*TODO*/
        // driver_sonicwi3,	/* 0097 (c) 1995 Video System Co. */
        /* 0098 Idol Mahjong - final romance 2 (CD only? not confirmed, MVS might exist) */
        /* 0099 */
        /*TODO*/
        // driver_turfmast,	/* 0200 (c) 1996 Nazca */
        /*TODO*/
        // driver_mslug,		/* 0201 (c) 1996 Nazca */
        /*TODO*/
        // driver_puzzledp,	/* 0202 (c) 1995 Taito (Visco license) */
        /*TODO*/
        // driver_mosyougi,	/* 0203 (c) 1995 ADK / SNK */
        /* 0204 ADK World (CD only) */
        /* 0205 Neo-Geo CD Special (CD only) */
        /*TODO*/
        // driver_marukodq,	/* 0206 (c) 1995 Takara */
        /*TODO*/
        // driver_neomrdo,	/* 0207 (c) 1996 Visco */
        /*TODO*/
        // driver_sdodgeb,	/* 0208 (c) 1996 Technos */
        /*TODO*/
        // driver_goalx3,	/* 0209 (c) 1995 Visco */
        /* 0210 */
        /* 0211 Oshidashi Zintrick (CD only? not confirmed, MVS might exist) */
        /*TODO*/
        // driver_overtop,	/* 0212 (c) 1996 ADK */
        /*TODO*/
        // driver_neodrift,	/* 0213 (c) 1996 Visco */
        /*TODO*/
        // driver_kof96,		/* 0214 (c) 1996 SNK */
        /*TODO*/
        // driver_ssideki4,	/* 0215 (c) 1996 SNK */
        /*TODO*/
        // driver_kizuna,	/* 0216 (c) 1996 SNK */
        /*TODO*/
        // driver_ninjamas,	/* 0217 (c) 1996 ADK / SNK */
        /*TODO*/
        // driver_ragnagrd,	/* 0218 (c) 1996 Saurus */
        /*TODO*/
        // driver_pgoal,		/* 0219 (c) 1996 Saurus */
        /* 0220 Choutetsu Brikin'ger - iron clad (MVS existance seems to have been confirmed) */
        /*TODO*/
        // driver_magdrop2,	/* 0221 (c) 1996 Data East Corporation */
        /*TODO*/
        // driver_samsho4,	/* 0222 (c) 1996 SNK */
        /*TODO*/
        // driver_rbffspec,	/* 0223 (c) 1996 SNK */
        /*TODO*/
        // driver_twinspri,	/* 0224 (c) 1996 ADK */
        /*TODO*/
        // driver_wakuwak7,	/* 0225 (c) 1996 Sunsoft */
        /* 0226 */
        /*TODO*/
        // driver_stakwin2,	/* 0227 (c) 1996 Saurus */
        /* 0228 */
        /* 0229 King of Fighters '96 CD Collection (CD only) */
        /*TODO*/
        // driver_breakers,	/* 0230 (c) 1996 Visco */
        /*TODO*/
        // driver_miexchng,	/* 0231 (c) 1997 Face */
        /*TODO*/
        // driver_kof97,		/* 0232 (c) 1997 SNK */
        /*TODO*/
        // driver_magdrop3,	/* 0233 (c) 1997 Data East Corporation */
        /*TODO*/
        // driver_lastblad,	/* 0234 (c) 1997 SNK */
        /*TODO*/
        // driver_puzzldpr,	/* 0235 (c) 1997 Taito (Visco license) */
        /*TODO*/
        // driver_irrmaze,	/* 0236 (c) 1997 SNK / Saurus */
        /*TODO*/
        // driver_popbounc,	/* 0237 (c) 1997 Video System Co. */
        /*TODO*/
        // driver_shocktro,	/* 0238 (c) 1997 Saurus */
        /*TODO*/
        // driver_shocktrj,	/* 0238 (c) 1997 Saurus */
        /*TODO*/
        // driver_blazstar,	/* 0239 (c) 1998 Yumekobo */
        /*TODO*/
        // driver_rbff2,		/* 0240 (c) 1998 SNK */
        /*TODO*/
        // driver_mslug2,	/* 0241 (c) 1998 SNK */
        /*TODO*/
        // driver_kof98,		/* 0242 (c) 1998 SNK */
        /*TODO*/
        // driver_lastbld2,	/* 0243 (c) 1998 SNK */
        /*TODO*/
        // driver_neocup98,	/* 0244 (c) 1998 SNK */
        /*TODO*/
        // driver_breakrev,	/* 0245 (c) 1998 Visco */
        /*TODO*/
        // driver_shocktr2,	/* 0246 (c) 1998 Saurus */
        /*TODO*/
        // driver_flipshot,	/* 0247 (c) 1998 Visco */
        /*TODO*/
        /// *TODO*///TESTDRIVER( pbobbl2n,	/* 0248 (c) 1999 Taito (SNK license) */
        /*TODO*/
        /// *TODO*///TESTDRIVER( ctomaday,	/* 0249 (c) 1999 Visco */
        /*TODO*/
        /// *TODO*///TESTDRIVER( mslugx,	/* 0250 (c) 1999 SNK */
        /*TODO*/
        /// *TODO*///TESTDRIVER( kof99,		/* 0251 (c) 1999 SNK */
        /*TODO*/
        /// *TODO*///TESTDRIVER( garou,		/* 0253 (c) 1999 SNK */
        /* Prehistoric Isle 2 */
        /* Strikers 1945 Plus */
        /* Ganryu */
        /**
         * done
         */
        /*RELEASE*///driver_retofinv,
        /*RELEASE*///driver_retofin1,
        /*RELEASE*///driver_retofin2,
        /*RELEASE*///driver_bagman,
        /*RELEASE*///driver_bagnard,
        /*RELEASE*///driver_bagmans,
        /*RELEASE*///driver_bagmans2,
        /*RELEASE*///driver_sbagman,
        /*RELEASE*///driver_sbagmans,
        /*RELEASE*///driver_pickin,
        /*RELEASE*///driver_hexa,
        /*RELEASE*///driver_wiping,
        /*RELEASE*///driver_rugrats,
        /*RELEASE*///driver_1942,
        /*RELEASE*///driver_1942a,
        /*RELEASE*///driver_1942b,
        /*RELEASE*///driver_1943,
        /*RELEASE*///driver_1943j,
        /*RELEASE*///driver_1943kai,
        /*RELEASE*///driver_bankp,
        /*RELEASE*///driver_ambush,
        driver_amidar,
        driver_amidaru,
        driver_amidaro,
        driver_amigo,
        driver_turtles,
        driver_turpin,
        driver_600,
        /*RELEASE*///driver_pengo,
        /*RELEASE*///driver_pengo2,
        /*RELEASE*///driver_pengo2u,
        /*RELEASE*///driver_penta,
        /*RELEASE*///driver_appoooh,
        /*RELEASE*///driver_zaxxon,
        /*RELEASE*///driver_zaxxon2,
        /*RELEASE*///driver_zaxxonb,
        /*RELEASE*///driver_szaxxon,
        /*RELEASE*///driver_futspy,
        /*RELEASE*///driver_razmataz,
        /*RELEASE*///driver_congo,
        /*RELEASE*///driver_tiptop,
        /*RELEASE*///driver_bking2,
        /*RELEASE*///driver_blueprnt,
        /*RELEASE*///driver_blueprnj,
        /*RELEASE*///driver_saturn,
        /*RELEASE*///driver_blktiger,
        /*RELEASE*///driver_bktigerb,
        /*RELEASE*///driver_blkdrgon,
        /*RELEASE*///driver_blkdrgnb,
        /*RELEASE*///driver_bombjack,
        /*RELEASE*///driver_bombjac2,
        /*RELEASE*///driver_pbaction,
        /*RELEASE*///driver_pbactio2,
        /*RELEASE*///driver_bosco,
        /*RELEASE*///driver_boscoo,
        /*RELEASE*///driver_boscoo2,
        /*RELEASE*///driver_boscomd,
        /*RELEASE*///driver_boscomdo,
        /*RELEASE*///driver_arabian,
        /*RELEASE*///driver_arabiana,
        /*RELEASE*///driver_cbasebal,
        /*RELEASE*///driver_zodiack,
        /*RELEASE*///driver_dogfight,
        /*RELEASE*///driver_moguchan,
        /*RELEASE*///driver_percuss,
        /*RELEASE*///driver_champbas,
        /*RELEASE*///driver_cheekyms,
        /*RELEASE*///driver_yamato,
        /*RELEASE*///driver_yamato2,
        /*RELEASE*///driver_xevious,
        /*RELEASE*///driver_xeviousa,
        /*RELEASE*///driver_xevios,
        /*RELEASE*///driver_sxevious,
        /*RELEASE*///driver_commando,
        /*RELEASE*///driver_commandu,
        /*RELEASE*///driver_commandj,
        /*RELEASE*///driver_spaceinv,
        /*RELEASE*///driver_cop01,
        /*RELEASE*///driver_cop01a,
        /*RELEASE*///driver_berzerk,
        /*RELEASE*///driver_berzerk1,
        /*RELEASE*///driver_frenzy,
        /*RELEASE*///driver_joyfulr,
        /*RELEASE*///driver_mnchmobl,
        /*RELEASE*///driver_airbustr,
        /*RELEASE*///driver_pingpong,
        driver_cclimber,
        driver_cclimbrj,
        driver_ccboot,
        driver_ccboot2,
        driver_ckong,
        driver_ckonga,
        driver_ckongjeu,
        driver_ckongo,
        driver_ckongalc,
        driver_monkeyd,
        driver_rpatrolb,
        driver_silvland,
        driver_swimmer,
        driver_swimmera,
        driver_guzzler,
        /*RELEASE*///driver_crbaloon,
        /*RELEASE*///driver_crbalon2,
        /*RELEASE*///driver_digdug,
        /*RELEASE*///driver_digdugb,
        /*RELEASE*///driver_digdugat,
        /*RELEASE*///driver_dzigzag,
        /*RELEASE*///driver_docastle,
        /*RELEASE*///driver_docastl2,
        /*RELEASE*///driver_douni,
        /*RELEASE*///driver_dorunrun,
        /*RELEASE*///driver_dorunru2,
        /*RELEASE*///driver_dorunruc,
        /*RELEASE*///driver_spiero,
        /*RELEASE*///driver_dowild,
        /*RELEASE*///driver_jjack,
        /*RELEASE*///driver_kickridr,
        /*RELEASE*///driver_espial,
        /*RELEASE*///driver_espiale,
        /*RELEASE*///driver_ttmahjng,
        /*RELEASE*///driver_jrpacman,
        /*RELEASE*///driver_exctsccr,
        /*RELEASE*///driver_exctscca,
        /*RELEASE*///driver_exctsccb,
        /*RELEASE*///driver_exedexes,
        /*RELEASE*///driver_savgbees,
        /*RELEASE*///driver_fastfred,
        /*RELEASE*///driver_jumpcoas,
        /*RELEASE*///driver_flyboyb,
        driver_frogger,
        driver_frogseg1,
        driver_frogseg2,
        driver_froggrmc,
        /*RELEASE*///driver_funkybee,
        /*RELEASE*///driver_skylancr,
        /*RELEASE*///driver_stinger,
        /*RELEASE*///driver_scion,
        /*RELEASE*///driver_scionc,
        /*RELEASE*///driver_wiz,
        /*RELEASE*///driver_wizt,
        /*RELEASE*///driver_solomon,
        /*RELEASE*///driver_hanaawas,
        /*RELEASE*///driver_higemaru,
        /*RELEASE*///driver_galaga,
        /*RELEASE*///driver_galagamw,
        /*RELEASE*///driver_galagads,
        /*RELEASE*///driver_gallag,
        /*RELEASE*///driver_galagab2,
        /*RELEASE*///driver_galaga84,
        /*RELEASE*///driver_nebulbee,
        /*RELEASE*///driver_galivan,
        /*RELEASE*///driver_galivan2,
        /*RELEASE*///driver_dangar,
        /*RELEASE*///driver_dangar2,
        /*RELEASE*///driver_dangarb,
        /*RELEASE*///driver_ninjemak,
        /*RELEASE*///driver_youma,
        /*RELEASE*///driver_pooyan,
        /*RELEASE*///driver_pooyans,
        /*RELEASE*///driver_pootan,
        /*RELEASE*///driver_gotya,
        /*RELEASE*///driver_mrjong,
        /*RELEASE*///driver_crazyblk,
        /*RELEASE*///driver_polyplay,
        /*RELEASE*///driver_magix,
        /*RELEASE*///driver_royalmah,
        /*RELEASE*///driver_sauro,
        /*RELEASE*///driver_goindol,
        /*RELEASE*///driver_homo,
        /*RELEASE*///driver_gundealr,
        /*RELEASE*///driver_gundeala,
        /*RELEASE*///driver_yamyam,
        /*RELEASE*///driver_wiseguy,
        /*RELEASE*///driver_gunsmoke,
        /*RELEASE*///driver_gunsmrom,
        /*RELEASE*///driver_gunsmoka,
        /*RELEASE*///driver_gunsmokj,
        /*RELEASE*///driver_jack,
        /*RELEASE*///driver_jack2,
        /*RELEASE*///driver_jack3,
        /*RELEASE*///driver_treahunt,
        /*RELEASE*///driver_zzyzzyxx,
        /*RELEASE*///driver_zzyzzyx2,
        /*RELEASE*///driver_brix,
        /*RELEASE*///driver_freeze,
        /*RELEASE*///driver_sucasino,
        /*RELEASE*///driver_fnkyfish,
        /*RELEASE*///driver_kangaroo,
        /*RELEASE*///driver_kangaroa,
        /*RELEASE*///driver_kangarob,
        /*RELEASE*///driver_ladybug,
        /*RELEASE*///driver_ladybugb,
        /*RELEASE*///driver_snapjack,
        /*RELEASE*///driver_cavenger,
        /*RELEASE*///driver_rallyx,
        /*RELEASE*///driver_rallyxm,
        /*RELEASE*///driver_nrallyx,
        /*RELEASE*///driver_nova2001,
        /*RELEASE*///driver_nov2001u,
        /*RELEASE*///driver_gsword,
        /*RELEASE*///driver_pkunwar,
        /*RELEASE*///driver_pkunwarj,
        /*RELEASE*///driver_speedbal,
        /*RELEASE*///driver_timeplt,
        /*RELEASE*///driver_timepltc,
        /*RELEASE*///driver_spaceplt,
        /*RELEASE*///driver_psurge,
        /*RELEASE*///driver_jungler,
        /*RELEASE*///driver_junglers,
        /*RELEASE*///driver_locomotn,
        /*RELEASE*///driver_gutangtn,
        /*RELEASE*///driver_cottong,
        /*RELEASE*///driver_commsega,
        /*RELEASE*///driver_minivadr,
        /*RELEASE*///driver_skyfox,
        /*RELEASE*///driver_exerizrb,
        /*RELEASE*///driver_empcity,
        /*RELEASE*///driver_empcityj,
        /*RELEASE*///driver_stfight,
        /*RELEASE*///driver_kchamp,
        /*RELEASE*///driver_karatedo,
        /*RELEASE*///driver_kchampvs,
        /*RELEASE*///driver_karatevs,
        /*RELEASE*///driver_naughtyb,
        /*RELEASE*///driver_naughtya,
        /*RELEASE*///driver_naughtyc,
        /*RELEASE*///driver_popflame,
        /*RELEASE*///driver_popflama,
        /*RELEASE*///driver_marineb,
        /*RELEASE*///driver_changes,
        /*RELEASE*///driver_looper,
        /*RELEASE*///driver_springer,
        /*RELEASE*///driver_hoccer,
        /*RELEASE*///driver_hoccer2,
        /*RELEASE*///driver_hopprobo,
        /*RELEASE*///driver_wanted,
        /*RELEASE*///driver_olibochu,
        /*RELEASE*///driver_suprloco,
        /*RELEASE*///driver_pipedrm,
        /*RELEASE*///driver_hatris,
        /*RELEASE*///driver_marvins,
        /*RELEASE*///driver_madcrash,
        /*RELEASE*///driver_vangrd2,
        /*RELEASE*///driver_plumppop,
        /*RELEASE*///driver_extrmatn,
        /*RELEASE*///driver_arkanoi2,
        /*RELEASE*///driver_ark2us,
        /*RELEASE*///driver_ark2jp,
        /*RELEASE*///driver_drtoppel,
        /*RELEASE*///driver_kageki,
        /*RELEASE*///driver_kagekij,
        /*RELEASE*///driver_chukatai,
        /*RELEASE*///driver_tnzs,
        /*RELEASE*///driver_tnzsb,
        /*RELEASE*///driver_tnzs2,
        /*RELEASE*///driver_insectx,
        /*RELEASE*///driver_route16,
        /*RELEASE*///driver_route16b,
        /*RELEASE*///driver_stratvox,
        /*RELEASE*///driver_stratvxb,
        /*RELEASE*///driver_speakres,
        /*RELEASE*///driver_sqixbl,
        /*RELEASE*///driver_ninjakd2,
        /*RELEASE*///driver_ninjak2a,
        /*RELEASE*///driver_ninjak2b,
        /*RELEASE*///driver_rdaction,
        /*RELEASE*///driver_galaxian,
        /*RELEASE*///driver_galmidw,
        /*RELEASE*///driver_superg,
        /*RELEASE*///driver_galaxb,
        /*RELEASE*///driver_galapx,
        /*RELEASE*///driver_galap1,
        /*RELEASE*///driver_galap4,
        /*RELEASE*///driver_galturbo,
        /*RELEASE*///driver_swarm,
        /*RELEASE*///driver_zerotime,
        /*RELEASE*///driver_pisces,
        /*RELEASE*///driver_uniwars,
        /*RELEASE*///driver_gteikoku,
        /*RELEASE*///driver_spacbatt,
        /*RELEASE*///driver_warofbug,
        /*RELEASE*///driver_redufo,
        /*RELEASE*///driver_exodus,
        /*RELEASE*///driver_pacmanbl,
        /*RELEASE*///driver_devilfsg,
        /*RELEASE*///driver_zigzag,
        /*RELEASE*///driver_zigzag2,
        /*RELEASE*///driver_jumpbug,
        /*RELEASE*///driver_jumpbugb,
        /*RELEASE*///driver_levers,
        /*RELEASE*///driver_azurian,
        /*RELEASE*///driver_orbitron,
        /*RELEASE*///driver_mooncrgx,
        /*RELEASE*///driver_mooncrst,
        /*RELEASE*///driver_mooncrsg,
        /*RELEASE*///driver_smooncrs,
        /*RELEASE*///driver_mooncrsb,
        /*RELEASE*///driver_mooncrs2,
        /*RELEASE*///driver_fantazia,
        /*RELEASE*///driver_eagle,
        /*RELEASE*///driver_eagle2,
        /*RELEASE*///driver_moonqsr,
        /*RELEASE*///driver_checkman,
        /*RELEASE*///driver_checkmaj,
        /*RELEASE*///driver_streakng,
        /*RELEASE*///driver_blkhole,
        /*RELEASE*///driver_moonal2,
        /*RELEASE*///driver_moonal2b,
        /*RELEASE*///driver_kingball,
        /*RELEASE*///driver_kingbalj,
        /*RELEASE*///driver_scramblb,
        /*RELEASE*///driver_mrdo,
        /*RELEASE*///driver_mrdot,
        /*RELEASE*///driver_mrdofix,
        /*RELEASE*///driver_mrlo,
        /*RELEASE*///driver_mrdu,
        /*RELEASE*///driver_mrdoy,
        /*RELEASE*///driver_yankeedo,
        /*RELEASE*///driver_sharkatt,
        /*RELEASE*///driver_thief,
        /*RELEASE*///driver_natodef,
        /*RELEASE*///driver_rygar,
        /*RELEASE*///driver_rygar2,
        /*RELEASE*///driver_rygarj,
        /*RELEASE*///driver_gemini,
        /*RELEASE*///driver_silkworm,
        /*RELEASE*///driver_silkwrm2,
        /*RELEASE*///driver_roundup,
        /*RELEASE*///driver_fitter,
        /*RELEASE*///driver_thepit,
        /*RELEASE*///driver_intrepid,
        /*RELEASE*///driver_intrepi2,
        /*RELEASE*///driver_portman,
        /*RELEASE*///driver_suprmous,
        /*RELEASE*///driver_suprmou2,
        /*RELEASE*///driver_machomou,
        driver_raimais,
        driver_kurikint,
        driver_kurikina,
        driver_fhawk,
        driver_plotting,
        driver_champwr,
        driver_champwru,
        driver_champwrj,
        driver_puzznic,
        driver_horshoes,
        driver_palamed,
        driver_cachat,
        driver_plgirls,
        driver_plgirls2,
        driver_gyrodine,
        driver_sonofphx,
        driver_repulse,
        driver_99lstwar,
        driver_99lstwra,
        driver_flashgal,
        driver_srdmissn,
        driver_airwolf,
        driver_skywolf,
        driver_skywolf2,
        driver_hal21,
        driver_hal21j,
        driver_aso,
        driver_seawolf2,
        driver_ebases,
        driver_wow,
        driver_robby,
        driver_lastday,
        driver_lastdaya,
        driver_pollux,
        driver_bluehawk,
        driver_primella,
        driver_cerberus,
        driver_mayhem,
        driver_wseries,
        driver_alleymas,
        driver_dangerz,
        driver_basebal2,
        driver_dblplay,
        driver_strkzone,
        driver_buggychl,
        driver_buggycht,
        driver_kicknrun,
        driver_mexico86,
        driver_spaceskr,
        driver_junglek,
        driver_junglkj2,
        driver_jungleh,
        driver_alpine,
        driver_alpinea,
        driver_timetunl,
        driver_wwestern,
        driver_wwester1,
        driver_frontlin,
        driver_elevator,
        driver_elevatob,
        driver_tinstar,
        driver_waterski,
        driver_bioatack,
        driver_hwrace,
        driver_sfposeid,
        driver_lsasquad,
        driver_storming,
        driver_arkanoid,
        driver_arknoidu,
        driver_arknoidj,
        driver_arkatayt,
        driver_arkbloc2,
        driver_arkangc,
        driver_arkatour,
        driver_spacefb,
        driver_spacefbg,
        driver_spacefbb,
        driver_spacebrd,
        driver_spacedem,
        driver_scobra,
        driver_scobras,
        driver_scobrab,
        driver_stratgyx,
        driver_stratgys,
        driver_armorcar,
        driver_armorca2,
        driver_moonwar2,
        driver_monwar2a,
        driver_spdcoin,
        driver_tazmania,
        driver_tazmani2,
        driver_calipso,
        driver_anteater,
        driver_rescue,
        driver_minefld,
        driver_losttomb,
        driver_losttmbh,
        driver_superbon,
        driver_hustler,
        driver_billiard,
        driver_hustlerb,
        driver_mario,
        driver_mariojp,
        driver_masao,
        driver_gyruss,
        driver_gyrussce,
        driver_venus,
        driver_tnk3,
        driver_tnk3j,
        driver_athena,
        driver_fitegolf,
        driver_ikari,
        driver_ikarijp,
        driver_ikarijpb,
        driver_victroad,
        driver_dogosoke,
        driver_gwar,
        driver_gwarj,
        driver_gwarb,
        driver_bermudat,
        driver_bermudaj,
        driver_bermudaa,
        driver_worldwar,
        driver_psychos,
        driver_psychosj,
        driver_chopper,
        driver_legofair,
        driver_ftsoccer,
        driver_tdfever,
        driver_tdfeverj,
        driver_vigilant,
        driver_vigilntu,
        driver_vigilntj,
        driver_kikcubic,
        driver_depthch,
        driver_safari,
        driver_frogs,
        driver_sspaceat,
        driver_sspacat2,
        driver_sspacatc,
        driver_headon,
        driver_headonb,
        driver_headon2,
        driver_invho2,
        driver_samurai,
        driver_invinco,
        driver_invds,
        driver_tranqgun,
        driver_spacetrk,
        driver_sptrekct,
        driver_carnival,
        driver_carnvckt,
        driver_digger,
        driver_pulsar,
        driver_heiankyo,
        driver_wc90,
        driver_wc90b,
        driver_sichuan2,
        driver_sichuana,
        driver_shisen,
        driver_tokiob,
        driver_bublbobl,
        driver_bublbobr,
        driver_bubbobr1,
        driver_boblbobl,
        driver_sboblbob,
        driver_lkage,
        driver_lkageb2,
        driver_lkageb3,
        driver_dotrikun,
        driver_dotriku2,
        driver_spacfury,
        driver_spacfura,
        driver_zektor,
        driver_tacscan,
        driver_elim2,
        driver_elim2a,
        driver_elim4,
        driver_startrek,
        driver_astrob,
        driver_astrob2,
        driver_005,
        driver_monsterb,
        driver_spaceod,
        driver_sindbadm,
        driver_skydiver,
        driver_monymony,
        driver_jackrabt,
        driver_jackrab2,
        driver_jackrabs,
        driver_friskyt,
        driver_radrad,
        driver_seicross,
        driver_sectrzon,
        driver_troangel,
        driver_mpatrol,
        driver_mpatrolw,
        driver_mranger,
        driver_yard,
        driver_vsyard,
        driver_vsyard2,
        driver_travrusa,
        driver_motorace,
        driver_circusc,
        driver_circusc2,
        driver_circuscc,
        driver_circusce,
        driver_mikie,
        driver_mikiej,
        driver_mikiehs,
        driver_sbasketb,
        driver_citycon,
        driver_citycona,
        driver_cruisin,
        driver_finalizr,
        driver_finalizb,
        driver_mappy,
        driver_mappyjp,
        driver_digdug2,
        driver_digdug2a,
        driver_todruaga,
        driver_todruagb,
        driver_motos,
        driver_phozon,
        driver_superpac,
        driver_superpcm,
        driver_pacnpal,
        driver_pacnpal2,
        driver_pacnchmp,
        driver_yiear,
        driver_yiear2,
        driver_ddribble,
        driver_sonson,
        driver_battlane,
        driver_battlan2,
        driver_battlan3,
        driver_brkthru,
        driver_brkthruj,
        driver_darwin,
        driver_gaplus,
        driver_gaplusa,
        driver_galaga3,
        driver_galaga3a,
        driver_grobda,
        driver_grobda2,
        driver_grobda3,
        driver_jailbrek,
        driver_junofrst,
        driver_junofstg,
        driver_megazone,
        driver_megaznik,
        driver_tutankhm,
        driver_tutankst,
        driver_rocnrope,
        driver_rocnropk,
        driver_ironhors,
        driver_dairesya,
        driver_trackfld,
        driver_trackflc,
        driver_hyprolym,
        driver_hyprolyb,
        driver_mystston,
        driver_bogeyman,
        driver_matmania,
        driver_excthour,
        driver_maniach,
        driver_maniach2,
        driver_renegade,
        driver_kuniokun,
        driver_kuniokub,
        driver_ddragon,
        driver_ddragonu,
        driver_ddragonb,
        driver_ddragon2,
        driver_atetris,
        driver_atetrisa,
        driver_atetrisb,
        driver_atetcktl,
        driver_atetckt2,
        driver_ccastles,
        driver_ccastle2,
        driver_missile,
        driver_missile2,
        driver_suprmatk,
        driver_asteroid,
        driver_asteroi1,
        driver_asteroib,
        driver_astdelux,
        driver_astdelu1,
        driver_llander,
        driver_llander1,
        driver_bwidow,
        driver_gravitar,
        driver_gravitr2,
        driver_spacduel,
        driver_kicker,
        driver_shaolins,
        driver_pandoras,
        driver_warlord,
        driver_centiped,
        driver_centipd2,
        driver_centipdb,
        driver_centipb2,
        driver_megatack,
        driver_killcom,
        driver_challeng,
        driver_kaos,
        driver_mhavoc,
        driver_mhavoc2,
        driver_mhavocp,
        driver_mhavocrv,
        driver_sidepckt,
        driver_sidepctj,
        driver_sidepctb,
        driver_ssozumo,
        driver_scregg,
        driver_eggs,
        driver_bigprowr,
        driver_tagteam,
        driver_xsleena,
        driver_xsleenab,
        driver_solarwar,
        /*
     * NOT Working flag
         */
        /*RELEASE*///driver_bounty,
        /*RELEASE*///driver_champbbj,
        /*RELEASE*///driver_champbb2,
        /*RELEASE*///driver_exctscc2,
        /*RELEASE*///driver_flyboy,
        /*RELEASE*///driver_mermaid,
        /*RELEASE*///driver_superqix,
        driver_kikikai,
        driver_kikstart,
        driver_arkbl2,
        driver_darkplnt,
        driver_gwara,
        driver_tokio,
        driver_lkageb,
        driver_astrob1,
        driver_farwest,
        /*
        NOT WORKING IN MAME EITHER
         */
        driver_pignewt,//bad roms?
        driver_pignewta,//bad roms??
        /*
        TEST DRIVERS
         */
        //TESTDRIVER( arkbl3,	
        //TESTDRIVER( arkblock,
        null
    };
}
