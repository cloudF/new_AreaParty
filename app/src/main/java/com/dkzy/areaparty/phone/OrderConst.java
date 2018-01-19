package com.dkzy.areaparty.phone;

/**
 * Created by boris on 2016/12/15.
 */

public class OrderConst {
    public static final int success = 200;
    public static final int failure = 404;
    public static final int somefailure = 405;

    public static final int refreshTab01MiddleImage_order = 0x100;
    public static final int refreshTab01ComputerActivity_order = 0x101;
    public static final int refreshTab01ComputerFragment01_order = 0x102;
    public static final int refreshTab01ComputerFragment02_order = 0x103;
    public static final int actionSuccess_order = 0x104;
    public static final int actionFail_order = 0x105;
    public static final int openFolder_order_successful = 0x106;
    public static final int openFolder_order_fail = 0x107;
    public static final int returnToParentFolder = 0x108;
    public static final int getDiskList_order_successful = 0x109;
    public static final int getDiskList_order_fail = 0x110;
    public static final int getExeList_order_successful = 0x111;
    public static final int getExeList_order_fail = 0x112;
    public static final int addSharedFilePath_successful = 0x114;
    public static final int addSharedFilePath_fail = 0x115;
    public static final int shareFileState = 0x118;
    public static final int setUserName = 0x119;

    public static final int getTVSYSApp_OK = 0x301;
    public static final int getTVOtherApp_OK = 0x302;
    public static final int getTVSYSApp_Fail = 0x303;
    public static final int getTVOtherApp_Fail = 0x304;
    public static final int getTVMouse_OK = 0x305;
    public static final int getTVMouse_Fail = 0x306;
    public static final int getPCApp_OK = 0x307;
    public static final int getPCApp_Fail = 0x308;
    public static final int getPCGame_OK = 0x309;
    public static final int getPCGame_Fail = 0x310;
    public static final int getTVInfor_OK = 0x311;
    public static final int getTVInfor_Fail = 0x312;
    public static final int openPCApp_OK = 0x313;
    public static final int openPCApp_Fail = 0x314;
    public static final int openPCGame_OK = 0x315;
    public static final int openPCGame_Fail = 0x316;
    public static final int getPCInfor_OK = 0x317;
    public static final int getPCInfor_Fail = 0x318;
    public static final int PCScreenLocked = 0x319;
    public static final int PCScreenNotLocked = 0x320;
    public static final int getPCMedia_OK = 0x201;
    public static final int getPCMedia_Fail = 0x202;
    public static final int playPCMedia_OK = 0x203;
    public static final int playPCMedia_Fail = 0x204;
    public static final int getPCRecentVideo_OK = 0x205;
    public static final int getPCRecentVideo_Fail = 0x206;
    public static final int getPCRecentAudio_OK = 0x207;
    public static final int getPCRecentAudio_Fail = 0x208;
    public static final int getPCAudioSets_OK = 0x209;
    public static final int getPCAudioSets_Fail = 0x210;
    public static final int getPCImageSets_OK = 0x211;
    public static final int getPCImageSets_Fail = 0x212;
    public static final int addPCSet_OK = 0x213;
    public static final int addPCSet_Fail = 0x214;
    public static final int addPCFilesToSet_OK = 0x215;
    public static final int addPCFilesToSet_Fail = 0x216;
    public static final int deletePCSet_OK = 0x217;
    public static final int deletePCSet_Fail = 0x218;
    public static final int playPCMediaSet_OK = 0x219;
    public static final int playPCMediaSet_Fail = 0x220;
    public static final int mediaAction_DELETE_OK = 0x221;
    public static final int mediaAction_DELETE_Fail = 0x222;

    public static final int addFriend_order = 0x600;
    public static final int getUserMsgFail_order = 0x601;
    public static final int friendUserLogIn_order = 0x602;
    public static final int shareUserLogIn_order = 0x603;
    public static final int netUserLogIn_order = 0x604;
    public static final int delFriend_order = 0x605;
    public static final int userFriendAdd_order = 0x606;
    public static final int userLogOut = 0x607;
    public static final int showUnfriendFiles = 0x608;
    public static final int showFriendFiles = 0x609;
    public static final int shareFileSuccess = 0x610;
    public static final int deleteShareFileSuccess = 0x614;
    public static final int shareFileFail = 0x611;
    public static final int addChatNum = 0x612;
    public static final int addFileRequest = 0x613;
    //downloadFragment
    public static final int torrentFileStartReq = 0x6100;
    public static final int torrentFilePauseReq = 0x6101;
    public static final int torrentFileStart = 0x6102;
    public static final int torrentFileContinue = 0x6103;
    public static final int torrentFileCancelReq = 0x6104;
    public static final int downloadFileContinue = 0x6105;
    public static final int agreeDownload = 0x6106;
    public static final int agreeDownloadState = 0x6107;
    public static final int downloadFileStartReq = 0x6108;
    public static final int downloadFilePauseReq = 0x6109;
    public static final int downloadFileCancelReq = 0x6110;
    public static final int downloadFilePause = 0x6111;
    public static final int torrentFilePause = 0x6112;
    public static final int downloadFileStart = 0x6113;


    public static final String monitorActionData_name = "MONITORDATA";
    public static final String monitorData_get_command = "GET";

    public static final String processAction_name = "PROCESS";
    public static final String process_closeById_command = "CLOSE";

    public static final String computerAction_name = "SERVERCOMPUTER";
    public static final String computerAction_reboot_command = "REBOOT";
    public static final String computerAction_shutdown_command = "SHUTDOWN";

    public static final String fileAction_name = "FILE";
    public static final String folderAction_name = "FOLDER";
    public static final String paramSourcePath = "-PATH-";
    public static final String paramTargetPath = "-PATH-";
    public static final String folderAction_openInComputer_more_param = "GETMORE";
    public static final String folderAction_openInComputer_more_message = "MOREFILE";
    public static final String folderAction_openInComputer_finish_param = "FINISHFILE";
    public static final String fileAction_openInComputer_command = "OPENFILE";
    public static final String folderAction_openInComputer_command = "OPENFOLDER";
    public static final String fileOrFolderAction_deleteInComputer_command = "DELETE";
    public static final String fileOrFolderAction_renameInComputer_command = "RENAME";
    public static final String folderAction_addInComputer_command = "ADDFOLDER";
    public static final String folderAction_addToList_command = "ADDTOHTTP";
    public static final String fileOrFolderAction_copy_command = "COPY";
    public static final String fileOrFolderAction_cut_command = "CUT";
    public static final String fileAction_share_command = "SHAREFILE";
    public static final String FOLDER_NASDELETE_command = "NASDELETE";

    public static final String ip_phone_source = "PHONE";
    public static final String ip_TV_source = "TV";
    public static final String ip_PC_B_source = "PC_B";
    public static final String ip_PC_Y_source = "PC_Y";
    public static final String ip_PC_monitor_founction = "MONITOR";
    public static final String ip_PC_action_founction = "ACTION";
    public static final String ip_default_type = "DEFAULT";

    public static final String diskAction_name = "DISK";
    public static final String diskAction_get_command = "GETDISKLIST";

    public static final String exeAction_name = "EXE";
    public static final String appAction_get_command = "GETEXELIST";
    public static final String exeAction_get_more_message = "MOREEXE";
    public static final String exeAction_get_finish_message = "FINISHEXE";
    public static final String exeAction_get_more_param = "GETMOREEXE";

    public static final String addPathToHttp_Name = "PC";
    public static final String addPathToHttp_command = "AddDirsHTTP";

    public static final String Media_Search_By_Key = "SEARCH";

    public static final String dlnaCastToTV_Command = "OPEN_HTTP_MEDIA";

    public static final String UTOrrent = "OPEN_UTORRENT";
    public static final String sysAction_name = "SYS";
    public static final String appAction_name = "APP";
    public static final String identityAction_name = "SECURITY";
    public static final String identityAction_command = "PAIR";
    public static final String videoAction_name = "VIDEO";
    public static final String audioAction_name = "AUDIO";
    public static final String gameAction_name = "GAME";
    public static final String imageAction_name = "IMAGE";
    public static final String sysAction_getScreenState_command = "GETSCREENSTATE";
    public static final String sysAction_getInfor_command = "GETINFOR";
    public static final String appMediaAction_getList_command = "GETTOTALLIST";
    public static final String appMediaAction_getRecent_command = "GETRECENTLIST";
    public static final String appAction_miracstOpen_command = "OPEN_MIRACAST";
    public static final String appAction_rdpOpen_command = "OPEN_RDP";
    public static final String mediaAction_play_command = "PLAY";
    public static final String mediaAction_playALL_command = "PLAYALL";
    public static final String mediaAction_playSet_command = "PLAYSET";
    public static final String mediaAction_playSet_command_BGM = "PLAYSETASBGM";
    public static final String mediaAction_DELETE_command = "DELETE";
    public static final String gameAction_open_command = "OPEN";
    public static final String gameAction_kill_command = "KILL";
    public static final String mediaAction_getSets_command = "GETSETS";
    public static final String mediaAction_addSet_command = "ADDSET";
    public static final String mediaAction_deleteSet_command = "DELETESET";
    public static final String mediaAction_addFilesToSet_command = "ADDFILESTOSET";

    public static final String getTVInfor_firCommand = "GET_TV_INFOR";
    public static final String getTVSYSApps_firCommand = "GET_TV_SYSAPPS";
    public static final String getTVOtherApps_firCommand = "GET_TV_INSTALLEDAPPS";
    public static final String getTVMouses_firCommand = "GET_TV_MOUSES";
    public static final String openTVApp_firCommand = "OPEN_TV_APPS";
    public static final String closeTVApp_firCommand = "CLOSE_TV_APPS";
    public static final String uninstallTVApp_firCommand = "UNINSTALL_TV_APPS";
    public static final String openTVRdp_firCommand = "OPEN_RDP";
    public static final String openTVAccessibility_firCommand = "OPEN_ACCESSIBILITY";
    public static final String openTVMiracast_firCommand = "OPEN_MIRACAST";
    public static final String shutdownTV_firCommand = "SHUTDOWN_TV";
    public static final String rebootTV_firCommand = "REBOOT_TV";
    public static final String openSettingTV_firCommand = "OPEN_TV_SETTINGPAGE";
    public static final String createPairWithPC_firCommand = "GAME_PAIR";
    public static final String startStreamWithPC_firCommand = "GAME_BEGIN_STREAMING";
    public static final String quitStreamWithPC_firCommand = "GAME_QUIT_STREAMING";
    public static final String createPairAndStreamWithPC_firCommand = "GAME_PLAY";
    public static final String getPCInfor_firCommand = "CURRENT_PC_INFOR";

    public static final String VLCAction_firCommand = "CONTROL_MEDIA";
    public static final String VLCAction_openMoonLightCommand = "OPEN_MOONLIGHT";
    public static final String VLCAction_Play_Pause_secondCommand = "PLAY_PAUSE";
    public static final String VLCAction_Play_secondCommand = "PLAY";
    public static final String VLCAction_Pause_secondCommand = "PAUSE";
    public static final String VLCAction_Stop_secondCommand = "STOP";
    public static final String VLCAction_Fast_secondCommand = "FAST_FORWARD";
    public static final String VLCAction_Rewind_SecondCommand = "REWIND";
    public static final String VLCAction_Exit_SecondCommand = "EXIT_PLAYER";
    public static final String VLCAction_Volume_Up_secondCommand = "VOLUME_UP";
    public static final String VLCAction_Volume_Down_secondCommand = "VOLUME_DOWN";
    public static final String VLCAction_BGM_secondCommand = "IMAGE_BACKGROUND_MUSIC";

    public static final String VLCAction_Appoint_Play_Position_secondCommand = "PLAY_APPOINT_POSITION";
    public static final String CHECK_ACCESSIBILITY_ISOPEN_firCommand= "CHECK_ACCESSIBILITY" ;

    public static final String VLCAction_HideSubtitle_SecondCommand = "HIDE_SUBTITLE";
    public static final String VLCAction_LoadSubtitle_SecondCommand = "LOAD_SUBTITLE";

    public static final String GET_AREAPARTY_PATH = "GETAREAPARTYPATH";

    public static final String VLCAction_Subtitle_Before_SecondCommand = "SUBTITLE_BEFORE";
    public static final String VLCAction_SubtitleDelay_SecondCommand = "SUBTITLE_DELAY";
    public static final String CLOSERDP = "RDP_BACK";

    public static final String GETDOWNLOADSTATE = "GETDOWNLOADSTATE";
    public static final String GETDOWNLOADProcess = "GETPROCESS";
    public static final String STOPDOWNLOAD = "STOPDOWNLOAD";
    public static final String RECOVERDOWNLOAD = "RECOVERDOWNLOAD";
    public static final String DELETEDOWNLOAD = "DELETEDOWNLOAD";
    // ...
}
