/*
 * Copyright (c) 2018. syshlang
 * @File: ShiroSessionDao.java
 * @Description:
 * @Author: sunys
 * @Date: 18-9-20 下午9:59
 * @since:
 */

package com.syshlang.system.sso.client.session;
import java.util.Date;

import com.syshlang.system.api.online.UserOnlineService;
import com.syshlang.system.sso.common.api.ShiroConstant;
import com.syshlang.system.sso.common.util.SerializeUtils;
import com.syshlang.system.model.online.entity.UserOnline;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * @author sunys
 */
public class ShiroSessionDao extends CachingSessionDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroSessionDao.class);

    private static String forceLogout="SHIRO_FORCE_LOGOUT";
    // 默认会话的过期时间
    private static final int SESSION_TIMEOUT = 300000;

    private String theWayCacheSession;

    @Autowired
    private UserOnlineService userOnlineService;


    /**
     * 读取Session,并重置过期时间
     * @param sessionId
     * @return
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        return readSessionFromCache(theWayCacheSession,sessionId);
    }


    private Session readSessionFromCache(String theWayCacheSession, Serializable sessionId) {
        LOGGER.debug("doReadSession >>>>> heWayCache={},sessionId={}", theWayCacheSession,sessionId);
        Session session = null;
        if (theWayCacheSession.equalsIgnoreCase(ShiroConstant.WAY_CACHESESSION.EHCACHE.getWay())){
            session =  super.getCachedSession(sessionId);
        } else if (theWayCacheSession.equalsIgnoreCase(ShiroConstant.WAY_CACHESESSION.DB.getWay())){
            String sessionIdStr = getSessionId(sessionId.toString());
            UserOnline userOnline = userOnlineService.selectUserOnlineBySessionId(sessionIdStr);
            if (userOnline != null){
                session =  SerializeUtils.deserialize(userOnline.getCode());
            }
        } else{

        }
        return session;
    }

    /**
     * 如DefaultSessionManager在创建完session后会调用该方法；
     * 如保存到关系数据库/文件系统/NoSQL数据库；即可以实现会话的持久化；
     * 返回会话ID；主要此处返回的ID.equals(session.getId())；
     * @param session  //http://sgq0085.iteye.com/blog/2170405
     * @return
     */
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        saveSession(theWayCacheSession,session);
        return sessionId;
    }

    private void saveSession(String theWayCacheSession, Session session){
        if (session  == null){
            return;
        }
        UserOnline userOnline = new UserOnline();
        String sessionId = getSessionId(session.getId().toString());
        LOGGER.debug("doCreate >>>>> heWayCache={},sessionId={}", theWayCacheSession,sessionId);
        userOnline.setSessionId(sessionId);
        //userOnline.setUserId(0L);
        userOnline.setCode(SerializeUtils.serialize(session));
        userOnline.setIpaddr(session.getHost());
        if (session instanceof UserSession){
            UserSession userSession = (UserSession) session;
            userOnline.setBrowser(userSession.getBrowser());
            userOnline.setOs(userSession.getOs());
        }
        userOnline.setStartTime(new Date());
        userOnline.setLastTime(new Date());
        userOnline.setExpireTime(session.getTimeout() / 1000 / 60);
        if (theWayCacheSession.equalsIgnoreCase(ShiroConstant.WAY_CACHESESSION.EHCACHE.getWay())){

        } else if (theWayCacheSession.equalsIgnoreCase(ShiroConstant.WAY_CACHESESSION.DB.getWay())){
            userOnlineService.insert(userOnline);
        } else {
        }
    }

    /**
     *  更新会话；如更新会话最后访问时间/停止会话/设置超时时间/设置移除属性等会调用
     * @param session
     */
    @Override
    protected void doUpdate(Session session) {
        // 如果会话过期/停止 没必要再更新了
        if(session instanceof ValidatingSession && !((ValidatingSession)session).isValid()) {
            return;
        }
        updateSession(theWayCacheSession,session);
    }

    private void updateSession(String theWayCacheSession, Session session) {
        // 更新session的最后一次访问时间
        UserSession userSession = (UserSession) session;
        UserSession userSessionCache = (UserSession) doReadSession(session.getId());
        if (userSessionCache != null){
            long deltaTime = System.currentTimeMillis()-userSessionCache.getLastAccessTime().getTime();
            if (deltaTime >= userSessionCache.getTimeout()){
                userSession.setStatus(UserSession.OnlineStatus.off_line);
                userSession.setAttribute("FORCE_LOGOUT","FORCE_LOGOUT");
            }else{
                userSession.setStatus(userSessionCache.getStatus());
                userSession.setAttribute("FORCE_LOGOUT", userSessionCache.getAttribute("FORCE_LOGOUT"));
            }
        }
        if (theWayCacheSession.equalsIgnoreCase(ShiroConstant.WAY_CACHESESSION.EHCACHE.getWay())){

        } else if (theWayCacheSession.equalsIgnoreCase(ShiroConstant.WAY_CACHESESSION.DB.getWay())){

        } else {

        }
    }

    /**
     * 删除会话；当会话过期/会话停止（如用户退出时）会调用
     * @param session
     */
    @Override
    protected void doDelete(Session session) {
        String sessionId = getSessionId(session.getId().toString());
    }


    private String getSessionId(String sessionId) {
        if (StringUtils.isBlank(sessionId)){
            return null;
        }
        return ShiroConstant.SYSHLANG_SYSTEM_USER_SERVER_SESSION_ID + "_" + sessionId;
    }

    /**
     *
     * @param ids
     * @return
     */
    public int forceout(String ids) {

        return 0;
    }

    public void setForceLogout(String forceLogout) {
        this.forceLogout = forceLogout;
    }

    public String getForceLogout() {
        return forceLogout;
    }

    public void setTheWayCacheSession(String theWayCacheSession) {
        if (StringUtils.isBlank(theWayCacheSession)){
            theWayCacheSession = ShiroConstant.WAY_CACHESESSION.DB.getWay();
        }
        this.theWayCacheSession = theWayCacheSession;
    }

    public String getTheWayCacheSession() {
        return theWayCacheSession;
    }
}
