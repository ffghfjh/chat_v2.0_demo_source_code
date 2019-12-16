package com.yiliaodemo.chat.constant;

import com.yiliaodemo.chat.BuildConfig;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：网络请求接口
 * 作者：
 * 创建时间：2018/6/25
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class ChatApi {

    private static final String SERVER = BuildConfig.hostAddress + "app/";
    private static final String SHARE = BuildConfig.hostAddress + "share/";

    /**
     * 发送短信验证码
     */
    public static final String SEND_SMS_CODE = SERVER + "sendPhoneVerificationCode.html";

    /**
     * 账号短信验证码登录
     */
    public static final String LOGIN = SERVER + "login.html";

    /**
     * 获取主页女神列表
     */
    public static final String GET_HOME_PAGE_LIST = SERVER + "getHomePageList.html";

    /**
     * 获取关注列表
     */
    public static final String GET_FOLLOW_LIST = SERVER + "getFollowList.html";

    /**
     * 选择性别
     */
    public static final String UPDATE_USER_SEX = SERVER + "upateUserSex.html";

    /**
     * 获取个人中心信息
     */
    public static final String INDEX = SERVER + "index.html";

    /**
     * 修改个人资料
     */
    public static final String UPDATE_PERSON_DATA = SERVER + "updatePersonalData.html";

    /**
     * 获取标签列表
     */
    public static final String GET_LABEL_LIST = SERVER + "getLabelList.html";

    /**
     * 获取个人资料
     */
    public static final String GET_PERSONAL_DATA = SERVER + "getPersonalData.html";

    /**
     * 微信登录获取access_token
     */
    public static final String WX_GET_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 微信登录获取user_info
     */
    public static final String WX_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 本app 微信登录
     */
    public static final String WE_CHAT_LOGIN = SERVER + "weixinLogin.html";

    /**
     * 本app QQ登录
     */
    public static final String QQ_LOGIN = SERVER + "qqLogin.html";

    /**
     * 获取主播资料
     */
    public static final String GET_ACTOR_INFO = SERVER + "getUserData.html";

    /**
     * 添加关注
     */
    public static final String SAVE_FOLLOW = SERVER + "saveFollow.html";

    /**
     * 取消关注
     */
    public static final String DEL_FOLLOW = SERVER + "delFollow.html";

    /**
     * 获取主播评论标签列表
     */
    public static final String GET_EVALUATION_LIST = SERVER + "getEvaluationList.html";

    /**
     * 获取主播视频或者照片
     */
    public static final String GET_DYNAMIC_LIST = SERVER + "getAlbumList.html";

    /**
     * 获取充值列表
     */
    public static final String GET_RECHARGE_DISCOUNT = SERVER + "getRechargeDiscount.html";

    /**
     * 验证昵称是否重复
     */
    public static final String GET_NICK_REPEAT = SERVER + "getNickRepeat.html";

    /**
     * 获取提现比例
     */
    public static final String GET_PUT_FORWARD_DISCOUNT = SERVER + "getPutforwardDiscount.html";

    /**
     * 申请体现
     */
    public static final String CONFIRM_PUT_FORWARD = SERVER + "confirmPutforward.html";

    /**
     * 删除我的相册
     */
    public static final String DEL_MY_PHOTO = SERVER + "delMyPhoto.html";

    /**
     * 获取钱包消费或者提现明细
     */
    public static final String GET_WALLET_DETAIL = SERVER + "getWalletDetail.html";

    /**
     * 获取推广赚钱
     */
    public static final String GET_SHARE_TOTAL = SERVER + "getShareTotal.html";

    /**
     * 获取我的推广用户列表
     */
    public static final String GET_SHARE_USER_LIST = SERVER + "getShareUserList.html";

    /**
     * 获取消息列表
     */
    public static final String GET_MESSAGE_LIST = SERVER + "getMessageList.html";

    /**
     * 获取钱包余额
     */
    public static final String GET_USER_BALANCE = SERVER + "getQueryUserBalance.html";

    /**
     * 投诉用户
     */
    public static final String SAVE_COMPLAINT = SERVER + "saveComplaint.html";

    /**
     * 获取视频上传签名
     */
    public static final String GET_VIDEO_SIGN = SERVER + "getVoideSign.html";

    /**
     * 新增相册数据
     */
    public static final String ADD_MY_PHOTO_ALBUM = SERVER + "addMyPhotoAlbum.html";

    /**
     * 实名认证
     */
    public static final String REAL_VERIFY = "http://netocr.com/verapi/veriden.html";

    /**
     * 提交实名认证资料
     */
    public static final String SUBMIT_VERIFY_DATA = SERVER + "submitIdentificationData.html";

    /**
     * 获取实名认证状态
     */
    public static final String GET_VERIFY_STATUS = SERVER + "getUserIsIdentification.html";

    /**
     * 获取主播收费设置
     */
    public static final String GET_ACTOR_CHARGE_SETUP = SERVER + "getAnchorChargeSetup.html";

    /**
     * 修改主播收费设置
     */
    public static final String UPDATE_CHARGE_SET = SERVER + "updateAnchorChargeSetup.html";

    /**
     * 获取主播播放页数据
     */
    public static final String GET_ACTOR_PLAY_PAGE = SERVER + "getAnchorPlayPage.html";

    /**
     * 添加意见反馈
     */
    public static final String ADD_FEED_BACK = SERVER + "addFeedback.html";

    /**
     * 视频聊天签名
     */
    public static final String GET_VIDEO_CHAT_SIGN = SERVER + "getVideoChatAutograph.html";

    /**
     * 用户对主播发起聊天
     */
    public static final String LAUNCH_VIDEO_CHAT = SERVER + "launchVideoChat.html";

    /**
     * 用户或者主播挂断链接
     */
    public static final String BREAK_LINK = SERVER + "breakLink.html";

    /**
     * 查看微信号码
     */
    public static final String SEE_WEI_XIN_CONSUME = SERVER + "seeWeiXinConsume.html";

    /**
     * 查看手机号
     */
    public static final String SEE_PHONE_CONSUME = SERVER + "seePhoneConsume.html";

    /**
     * 修改手机号码
     */
    public static final String UPDATE_PHONE = SERVER + "updatePhone.html";

    /**
     * 非VIP查看私密照片
     */
    public static final String SEE_IMAGE_CONSUME = SERVER + "seeImgConsume.html";

    /**
     * 非VIP查看私密视频
     */
    public static final String SEE_VIDEO_CONSUME = SERVER + "seeVideoConsume.html";

    /**
     * 非VIP查看私密视频
     */
    public static final String VIP_SEE_DATA = SERVER + "vipSeeData.html";

    /**
     * 开始计时
     */
    public static final String VIDEO_CHAT_BIGIN_TIMING = SERVER + "videoCharBeginTiming.html";

    /**
     * 设置勿扰
     */
    public static final String UPDATE_USER_DISTURB = SERVER + "updateUserDisturb.html";

    /**
     * 获取短视频列表
     */
    public static final String GET_VIDEO_LIST = SERVER + "getVideoList.html";

    /**
     * 非VIP发送文本消息
     */
    public static final String SEND_TEXT_CONSUME = SERVER + "sendTextConsume.html";

    /**
     * 发红包
     */
    public static final String SEND_RED_ENVELOPE = SERVER + "sendRedEnvelope.html";

    /**
     * 获取礼物列表
     */
    public static final String GET_GIFT_LIST = SERVER + "getGiftList.html";

    /**
     * 用户赠送礼物
     */
    public static final String USER_GIVE_GIFT = SERVER + "userGiveGift.html";

    /**
     * 获取用户收未拆开红包统计
     */
    public static final String GET_RED_PACKET_COUNT = SERVER + "getRedPacketCount.html";

    /**
     * 用户拆开红包
     */
    public static final String RECEIVE_RED_PACKET = SERVER + "receiveRedPacket.html";

    /**
     * 获取VIP套餐列表
     */
    public static final String GET_VIP_SET_MEAL_LIST = SERVER + "getVIPSetMealList.html";

    /**
     * VIP支付
     */
    public static final String VIP_STORE_VALUE = SERVER + "vipStoreValue.html";

    /**
     * 金币充值
     */
    public static final String GOLD_STORE_VALUE = SERVER + "goldStoreValue.html";

    /**
     * 评价主播
     */
    public static final String SAVE_COMMENT = SERVER + "saveComment.html";

    /**
     * 获取用户可提现金币
     */
    public static final String GET_USEABLE_GOLD = SERVER + "getUsableGold.html";

    /**
     * 更新提现资料
     */
    public static final String MODIFY_PUT_FORWARD_DATA = SERVER + "modifyPutForwardData.html";

    /**
     * 获取欢迎消息
     */
    public static final String GET_PUSH_MSG = SERVER + "getPushMsg.html";

    /**
     * 用户登出
     */
    public static final String LOGOUT = SERVER + "logout.html";

    /**
     * 获取最新版本
     */
    public static final String GET_NEW_VERSION = SERVER + "getNewVersion.html";

    /**
     * 主页搜索
     */
    public static final String GET_SEARCH_LIST = SERVER + "getSearchList.html";

    /**
     * 加载用户列表
     */
    public static final String GET_ONLINE_USER_LIST = SERVER + "getOnLineUserList.html";

    /**
     * 主播对用户发起聊天
     */
    public static final String ACTOR_LAUNCH_VIDEO_CHAT = SERVER + "anchorLaunchVideoChat.html";

    /**
     * 获取用户是否新用户
     */
    public static final String GET_USER_NEW = SERVER + "getUserNew.html";

    /**
     * 1.1版更新用户登陆时间
     */
    public static final String UP_LOGIN_TIME = SERVER + "upLoginTime.html";

    /**
     * 获取banner列表
     */
    public static final String GET_BANNER_LIST = SERVER + "getBannerList.html";

    /**
     * 1.2版 统计公会主播数和贡献值
     */
    public static final String GET_GUILD_COUNT = SERVER + "getGuildCount.html";

    /**
     * 1.2版 获取公会主播贡献列表
     */
    public static final String GET_CONTRIBUTION_LIST = SERVER + "getContributionList.html";

    /**
     * 1.2版 拉取是否邀请主播加入公会
     */
    public static final String GET_ANCHOR_ADD_GUILD = SERVER + "getAnchorAddGuild.html";

    /**
     * 1.2版 申请公会
     */
    public static final String APPLY_GUILD = SERVER + "applyGuild.html";

    /**
     * 1.2版 主播确认是否加入公会
     */
    public static final String IS_APPLY_GUILD = SERVER + "isApplyGuild.html";

    /**
     * 1.2版 获取公会主播贡献明细统计
     */
    public static final String GET_ANTHOR_TOTAL = SERVER + "getAnthorTotal.html";

    /**
     * 1.2版 公会主播贡献明细列表
     */
    public static final String GET_CONTRIBUTION_DETAIL = SERVER + "getContributionDetail.html";

    /**
     * 1.2版 获取礼物赠送列表
     */
    public static final String GET_REWARD_LIST = SERVER + "getRewardList.html";

    /**
     * 1.2版 获取提现方式列表
     */
    public static final String GET_TAKE_OUT_MODE = SERVER + "getTakeOutMode.html";

    /**
     * 1.2版 申请CPS联盟
     */
    public static final String ADD_CPS_MS = SERVER + "addCpsMs.html";

    /**
     * 1.2版 CPS联盟统计
     */
    public static final String GET_TOTAL_DATEIL = SERVER + "getTotalDateil.html";

    /**
     * 1.2版 CPS获取用户贡献列表
     */
    public static final String GET_MY_CONTRIBUTION_LIST = SERVER + "getMyContributionList.html";

    /**
     * 1.3版 获取魅力榜
     */
    public static final String GET_GLAMOUR_LIST = SERVER + "getGlamourList.html";

    /**
     * 1.3版 获取消费榜
     */
    public static final String GET_CONSUME_LIST = SERVER + "getConsumeList.html";

    /**
     * 1.3版 获取豪礼榜
     */
    public static final String GET_COURTESY_LIST = SERVER + "getCourtesyList.html";

    /**
     * 1.3版 主播收益明细
     */
    public static final String GET_ANCHOR_PROFIT_DETAIL = SERVER + "getAnchorProfitDetail.html";

    /**
     * 1.3.1 新增动态查看次数
     */
    public static final String ADD_QUERY_DYNAMIC_COUNT = SERVER + "addQueryDynamicCount.html";

    /**
     * 1.3.1 用户点赞
     */
    public static final String ADD_LAUD = SERVER + "addLaud.html";

    /**
     * 1.3.1 用户取消点赞
     */
    public static final String CANCEL_LAUD = SERVER + "cancelLaud.html";

    /**
     * 1.3.1 获取推荐主播
     */
    public static final String GET_HOME_NOMINATE_LIST = SERVER + "getHomeNominateList.html";

    /**
     * 1.3.1 获取试看主播列表
     */
    public static final String GET_TRY_COMPERE_LIST = SERVER + "getTryCompereList.html";

    /**
     * 1.3.1 获取新人主播
     */
    public static final String GET_NEW_COMPERE_LIST = SERVER + "getNewCompereList.html";

    /**
     * 1.3.2 版 获推广奖励规则
     */
    public static final String GET_SPREAD_AWARD = SERVER + "getSpreadAward.html";

    /**
     * 1.3.2 版 获取推荐贡献排行榜
     */
    public static final String GET_SPREAD_BONUSES = SERVER + "getSpreadBonuses.html";

    /**
     * 1.3.2 版 获取推荐用户排行榜
     */
    public static final String GET_SPREAD_USER = SERVER + "getSpreadUser.html";

    /**
     * 添加分享次数
     */
    public static final String ADD_SHARE_COUNT = SHARE + "addShareCount.html";

    /**
     * 1.1小夫妻分享
     */
    public static final String JUMP_SPOUSE = SHARE + "jumpSpouse.html?userId=";

    /**
     * 1.1主播招募分享
     */
    public static final String JUMP_ANCHORS = SHARE + "jumpAnchors.html?userId=";

    /**
     * 分享链接
     */
    public static final String SHARE_URL = SHARE + "jumpShare.html?userId=";

    /**
     * 1.1版 开红包游戏分享
     */
    public static final String JUMP_GAME = SHARE + "jumpGame.html?userId=";

    //-----------------------其他相关------------

    /**
     * CPS 分享 好友邀请你聊天链接
     */
    public static final String CHAT_OFFICAL_URL = "http://www.1-liao.com";

    //-------------------1.4版-------------

    /**
     * 1.4版 获取主播亲密排行和礼物排行
     */
    public static final String GET_INITMATE_AND_GIFT = SERVER + "getIntimateAndGift.html";

    /**
     * 1.4版 获取主播亲密排行和礼物排行
     */
    public static final String GET_ANTHOR_INTIMATE_LIST = SERVER + "getAnthorIntimateList.html";

    /**
     * 1.4版 获取主播亲密排行和礼物排行
     */
    public static final String GET_ANTHOR_GIFT_LIST = SERVER + "getAnthorGiftList.html";

    /**
     * 1.4版 钱包头部统计
     */
    public static final String GET_PROFIT_AND_PAY_TOTAL = SERVER + "getProfitAndPayTotal.html";

    /**
     * 1.4版 获取钱包明细
     */
    public static final String GET_USER_GOLD_DETAILS = SERVER + "getUserGoldDetails.html";

    /**
     * 1.4 版 获取我的相册列表
     */
    public static final String GET_MY_ANNUAL_ALBUM = SERVER + "getMyAnnualAlbum.html";

    /**
     * 1.4 版 获取指定用户是否关注
     */
    public static final String GET_SPECIFY_USER_FOLLOW = SERVER + "getSpecifyUserFollow.html";

    /**
     * 1.4 版 获取通话记录
     */
    public static final String GET_CALL_LOG = SERVER + "getCallLog.html";

    /**
     * 1.4版  获取收费设置
     */
    public static final String GET_ANTHOR_CHARGE_LIST = SERVER + "getAnthorChargeList.html";

    /**
     * 1.4版  获取认证微信号
     */
    public static final String GET_IDENTIFICATION_WEI_XIN = SERVER + "getIdentificationWeiXin.html";

    /**
     * 1.4版  获取私密视频收费设置
     */
    public static final String GET_PRIVATE_VIDEO_MONEY = SERVER + "getPrivateVideoMoney.html";

    /**
     * 1.4版  获取私密视频收费设置
     */
    public static final String GET_PRIVATE_PHOTO_MONEY = SERVER + "getPrivatePhotoMoney.html";

    /**
     * 1.5版  上传坐标
     */
    public static final String UPLOAD_COORDINATE = SERVER + "uploadCoordinate.html";

    /**
     * 1.5版  获取附近用户列表
     */
    public static final String GET_ANTHOR_DISTANCE_LIST = SERVER + "getAnthorDistanceList.html";

    /**
     * 1.5版  获取附近的用户列表
     */
    public static final String GET_NEAR_BY_LIST = SERVER + "getNearbyList.html";

    /**
     * 1.5版 获取用户信息
     */
    public static final String GET_USER_DETA = SERVER + "getUserDeta.html";

    /**
     * 用户编号挂断链接
     */
    public static final String USER_HANG_UP_LINK = SERVER + "userHangupLink.html";

    /**
     * 获取未读消息数
     */
    public static final String GET_UN_READ_MESSAGE = SERVER + "getUnreadMessage.html";

    /**
     * 设置为已读
     */
    public static final String SET_UP_READ = SERVER + "setupRead.html";

    /**
     * 设置为已读
     */
    public static final String UNITE_ID_CARD = SERVER + "uniteIdCard.html";

    /**
     * 1.6版 获取动态列表
     */
    public static final String GET_USER_DYNAMIC_LIST = SERVER + "getUserDynamicList.html";

    /**
     * 1.6版 发布动态
     */
    public static final String RELEASE_DYNAMIC = SERVER + "releaseDynamic.html";

    /**
     * 1.6版 添加点赞
     */
    public static final String GIVE_THE_THUMB_UP = SERVER + "giveTheThumbsUp.html";

    /**
     * 1.6版 获取评论列表
     */
    public static final String GET_COMMENT_LIST = SERVER + "getCommentList.html";

    /**
     * 1.6版 添加评论
     */
    public static final String DISCUSS_DYNAMIC = SERVER + "discussDynamic.html";

    /**
     * 1.6版 删除动态评论
     */
    public static final String DEL_COMMENT = SERVER + "delComment.html";

    /**
     * 1.6版 删除动态评论
     */
    public static final String DYNAMIC_PAY = SERVER + "dynamicPay.html";

    /**
     * 1.6版 获取最新评论列表
     */
    public static final String GET_USER_NEW_COMMENT = SERVER + "getUserNewComment.html";

    /**
     * 1.6版 获取通知记录数或者新动态
     */
    public static final String GET_USER_DYNAMIC_NOTICE = SERVER + "getUserDynamicNotice.html";

    /**
     * 1.6版 获取图形验证码流
     */
    public static final String GET_VERIFY = SERVER + "getVerify.html?phone=";

    /**
     * 1.6版 获取通知记录数或者新动态
     */
    public static final String GET_VERIFY_CODE_IS_CORRECT = SERVER + "getVerifyCodeIsCorrect.html";

    /**
     * 1.6版 获取自己的动态
     */
    public static final String GET_OWN_DYNAMIC_LIST = SERVER + "getOwnDynamicList.html";

    /**
     * 1.6版 删除动态
     */
    public static final String DEL_DYNAMIC = SERVER + "delDynamic.html";

    /**
     * 1.7版 主播获取速配房间
     */
    public static final String GET_SPEED_DATING_ROOM = SERVER + "getSpeedDatingRoom.html";

    /**
     * 1.7版 开启速配
     */
    public static final String OPEN_SPEED_DATING = SERVER + "openSpeedDating.html";

    /**
     * 1.7版 主播结束速配
     */
    public static final String END_SPEED_DATING = SERVER + "appEndSpeedDating.html";

    /**
     * 1.7版 用户拉取速配主播
     */
    public static final String GET_SPEED_DATING_ANCHOR = SERVER + "getSpeedDatingAnchor.html";

    /**
     * 获取客服QQ
     */
    public static final String GET_SERVICE_QQ = SERVER + "getServiceQQ.html";

    /**
     * 1.7版 获取主播速配时长
     */
    public static final String GET_USER_SPEED_TIME = SERVER + "getUserSpeedTime.html";

    /**
     * 获取APP外部下载地址
     */
    public static final String GET_DOLOAD_URL = SHARE + "getDoloadUrl.html";

    /**
     * 1.7版 获取帮助列表
     */
    public static final String GET_HELP_CONTRE = SERVER + "getHelpContre.html";

    /**
     * 1.6版 获取主播动态列表
     */
    public static final String GET_PRIVATE_DYNAMIC_LIST = SERVER + "getPrivateDynamicList.html";

    /**
     * 1.8版 获取分享图片
     */
    public static final String ON_LOAD_GALANCE_OVER = SERVER + "onloadGlanceOver.html?userId=";

    /**
     * 用户注册账号
     */
    public static final String REGISTER = SERVER + "register.html";

    /**
     * 忘记密码重置
     */
    public static final String UP_PASSWORD = SERVER + "upPassword.html";

    /**
     * 账号密码登陆
     */
    public static final String USER_LOGIN = SERVER + "userLogin.html";

    //-----------------------------大房间----------------------------
    /**
     * 获取大房间列表
     */
    public static final String GET_BIG_ROOM_LIST = SERVER + "getBigRoomList.html";

    /**
     * 获取主播封面
     */
    public static final String GET_USER_COVER_IMG = SERVER + "getUserCoverImg.html";

    /**
     * 主播开启直播
     */
    public static final String OPEN_LIVE_TELECAST = SERVER + "openLiveTelecast.html";

    /**
     * 用户加入直播间
     */
    public static final String USER_MIX_BIG_ROOM = SERVER + "userMixBigRoom.html";

    /**
     * 主播关闭或者暂停直播
     */
    public static final String CLOSE_LIVE_TELECAST = SERVER + "closeLiveTelecast.html";

    /**
     * 获取直播间的贡献榜
     */
    public static final String GET_BIG_CONTRIBUTION_LIST = SERVER + "getBigContributionList.html";

    /**
     * 获取直播间的在线观众
     */
    public static final String GET_ROOM_USER_LIST = SERVER + "getRoomUserList.html";

    /**
     * 点击头像获取信息
     */
    public static final String GET_USER_INDEX_DATA = SERVER + "getUserIndexData.html";

    /**
     * 用户退出直播间
     */
    public static final String USER_QUIT_BIG_ROOM = SERVER + "userQuitBigRoom.html";

}

