package com.hayden.hap.common.git.itf;

import com.hayden.hap.common.spring.service.IService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;

import java.io.IOException;
import java.util.List;


/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/4 14:35
 */
@IService("gitService")
public interface IGitService {
    /**
     * 克隆远程git资源，如果本地已存在则打开本地git目录
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    Git cloneRemote() throws GitAPIException, IOException;

    List<PushResult> addAndPush(String gitPath, String message) throws GitAPIException, IOException;

    List<PushResult> addAndPush(String message) throws GitAPIException, IOException;

    /**
     * 克隆或者pull远程资源,本地git目录不存在则克隆，存在则pull,pull前进行clean操作
     * @return
     * @throws GitAPIException
     * @throws IOException
     */
    PullResult cloneOrPull() throws GitAPIException, IOException;

    /**
     * pull远程代码，pull前clean
     * @param git
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    PullResult cleanAndPull(Git git) throws IOException, GitAPIException;

    /**
     * pull远程代码
     * @param git
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    PullResult pull(Git git) throws IOException, GitAPIException;
}
