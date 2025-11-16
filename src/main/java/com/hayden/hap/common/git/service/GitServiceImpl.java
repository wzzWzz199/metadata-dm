package com.hayden.hap.common.git.service;


import com.hayden.hap.common.git.itf.IGitService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/4 15:26
 */
@Service("gitService")
public class GitServiceImpl implements IGitService {
    @Value("${META.GIT.GITURL}")
    private String GITURL;
    @Value("${META.GIT.GITUSER}")
    private String GITUSER;
    @Value("${META.GIT.GITPASSWORD}")
    private String GITPASSWORD;
    @Value("${META.GIT.ROOTPATH}")
    private String GITROOTPATH;

    @Override
    public Git cloneRemote() throws GitAPIException, IOException {
        File rootFile = new File(GITROOTPATH);
        if (!rootFile.exists()) {
            Git git = Git.cloneRepository()
                    .setURI(GITURL)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(GITUSER, GITPASSWORD))
                    .setBranch("master")
                    .setDirectory(new File(GITROOTPATH))
                    .call();
            return git;
        } else {
            return (Git.open(new File(GITROOTPATH)));
        }
    }

    @Override
    public List<PushResult> addAndPush(String filePath,String message) throws GitAPIException, IOException {
        Git git = cloneRemote();
        git.add().addFilepattern(filePath).call();
        git.commit().setMessage(message).call();
        List<PushResult> pushResults= (List<PushResult>) git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(GITUSER, GITPASSWORD)).call();
        return pushResults;
    }

    @Override
    public List<PushResult> addAndPush(String message) throws GitAPIException, IOException {
          return this.addAndPush(".",message);
    }

    @Override
    public PullResult cloneOrPull() throws GitAPIException, IOException {
        Git git = cloneRemote();
        PullResult pull = cleanAndPull(git);
        git.close();
        return pull;
    }

    @Override
    public PullResult cleanAndPull(Git git) throws IOException, GitAPIException {
        git.clean();
        git.reset();
        PullResult pull = pull(git);
        git.clean();
        git.reset();
        return pull;
    }

    @Override
    public PullResult pull(Git git) throws IOException, GitAPIException {
        PullResult pull = git.pull().setCredentialsProvider(new UsernamePasswordCredentialsProvider(GITUSER, GITPASSWORD)).call();
        return pull;
    }
}
