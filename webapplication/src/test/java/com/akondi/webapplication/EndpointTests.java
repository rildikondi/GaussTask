package com.akondi.webapplication;

import com.akondi.interfaceadapters.presenters.getgausssolutions.GaussSolutionsPresenterOutputBoundary;
import com.akondi.interfaceadapters.presenters.gausssolve.GaussSolvePresenterOutputBoundary;
import com.akondi.interfaceadapters.viewmodels.GaussSolutionViewModel;
import com.akondi.interfaceadapters.viewmodels.GaussSolutionsViewModel;
import com.akondi.interfaceadapters.viewmodels.GaussSolveViewModel;
import com.akondi.usecases.database.Database;
import com.akondi.usecases.database.GaussSolutionGateway;
import com.akondi.usecases.gausssolve.Clock;
import com.akondi.usecases.gausssolve.GaussSolveInputBoundary;
import com.akondi.usecases.gausssolve.IdGenerator;
import com.akondi.usecases.get.gausssolutions.GetGaussSolutionsInputBoundary;
import com.akondi.webapplication.endpoints.gausssolve.GaussSolveEndPoint;
import com.akondi.webapplication.endpoints.getsolutions.GetGaussSolutionsEndpoint;
import com.akondi.webapplication.exception.ErrorMessageMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {GaussSolveEndPoint.class, GetGaussSolutionsEndpoint.class})
@AutoConfigureJsonTesters
@WebAppConfiguration
public class EndpointTests {
    private final double[][] a = {{2.0, 4.0}, {5.0, -6.0}};
    private final double[] b = {8.0, 4.0};
    private final double[] solution = {2.0, 1.0};


    private final String DESCRIPTION = "description-1";

    private final String START_DATE = "2018-01-01";
    private final String END_DATE = "2018-01-31";
    private final String STATUS = "ACTIVE";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GaussSolveInputBoundary gaussSolveInputBoundary;
    @MockBean
    private GaussSolvePresenterOutputBoundary gaussSolvePresenterOutputBoundary;
    @MockBean
    private Database database;
    @MockBean
    private GetGaussSolutionsInputBoundary getGaussSolutionsInputBoundary;
    @MockBean
    private GaussSolutionsPresenterOutputBoundary gaussSolutionsPresenterOutputBoundary;
    @MockBean
    private Clock clock;
    @MockBean
    private IdGenerator idGenerator;

    private byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    private String convertToJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.writeValueAsString(object);
    }

    @Before
    public void setUp() {
        when(clock.now()).thenReturn(LocalDate.of(2018, 01, 15));
    }

    @Test
    public void can_solve_linear_equations() throws Exception {
        NewGaussSolveRequest request = new NewGaussSolveRequest(a, b);
        when(gaussSolvePresenterOutputBoundary.getViewModel()).thenReturn(new GaussSolveViewModel(solution));
        mockMvc.perform(
                post(URI.create("/api/v1/gausssolver"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(convertObjectToJsonBytes(request))
        )
                .andExpect(status().isOk());
    }

    @Test
    public void cannot_solve_gaussSolutions_when_bad_input() throws Exception {
        NewGaussSolveRequest request = new NewGaussSolveRequest(null, null);
        doThrow(GaussSolutionGateway.EquationSystemBadDataException.class).when(gaussSolveInputBoundary).execute(any());
        mockMvc.perform(
                post(URI.create("/api/v1/gausssolver"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(convertObjectToJsonBytes(request))
        )
                .andExpect(status().isBadRequest());
                //.andExpect(jsonPath("$.errors.[0]", is(equalTo(ErrorMessageMap.errors.get(GaussSolutionGateway.EquationSystemBadDataException.class)))));
    }


    @Test
    public void cannot_get_gaussSolutions_when_they_not_exist() throws Exception {
        doThrow(GaussSolutionGateway.GaussSolutionsDataNotFoundException.class).when(getGaussSolutionsInputBoundary).execute(any());
        mockMvc.perform(
                get("/api/v1/gausssolver")
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors.[0]", is(equalTo(ErrorMessageMap.errors.get(GaussSolutionGateway.GaussSolutionsDataNotFoundException.class)))));
    }

    @Test
    public void can_get_gaussSolutions() throws Exception {
        long id = idGenerator.getNextId();
        String date = clock.now().toString();
        NewGaussSolveRequest request = new NewGaussSolveRequest(a, b);
        String document = convertToJsonString(request);
        String result = convertToJsonString(solution);

        GaussSolutionViewModel gaussSolutionViewModel = new GaussSolutionViewModel(id, date, document, result);
        List<GaussSolutionViewModel> solutionList = new ArrayList<>();
        solutionList.add(gaussSolutionViewModel);
        GaussSolutionsViewModel gaussSolutionsViewModel = new GaussSolutionsViewModel(solutionList);

        when(gaussSolutionsPresenterOutputBoundary.getViewModel()).thenReturn(gaussSolutionsViewModel);
        mockMvc.perform(
                get("/api/v1/gausssolver")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solutionsData", hasSize(1)))
                .andExpect(jsonPath("$.solutionsData.[0].id", is(equalTo((int) id))))
                .andExpect(jsonPath("$.solutionsData.[0].date", is(equalTo(date))))
                .andExpect(jsonPath("$.solutionsData.[0].document", is(equalTo(document))))
                .andExpect(jsonPath("$.solutionsData.[0].result", is(equalTo(result))));
    }

    private class NewGaussSolveRequest {
        private final double[][] a;
        private final double[] b;

        public NewGaussSolveRequest(double[][] a, double[] b) {
            this.a = a;
            this.b = b;
        }

        public double[][] getA() {
            return a;
        }

        public double[] getB() {
            return b;
        }
    }

}
