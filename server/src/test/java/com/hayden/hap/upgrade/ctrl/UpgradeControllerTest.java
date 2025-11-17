package com.hayden.hap.upgrade.ctrl;

import com.hayden.hap.common.formmgr.message.Status;
import com.hayden.hap.upgrade.itf.IUpgradeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UpgradeController.class)
class UpgradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUpgradeService upgradeService;

    @Test
    void getModuleListReturnsData() throws Exception {
        Mockito.when(upgradeService.getModuListWithSync(any()))
                .thenReturn(Collections.singletonList("core"));

        mockMvc.perform(get("/metadata/UPGRADE/getModuleList")
                        .param("project", "demo")
                        .param("env", "dev"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(Status.SUCCESS))
                .andExpect(jsonPath("$.data[0]").value("core"));
    }
}
