package ADPTest.controllers;

import ADPTest.Application;
import ADPTest.Output;
import ADPTest.domain.ExchangeAmount;
import ADPTest.domain.StringConstants;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.equalTo;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class MachineControllerTest {

    @Autowired
    private MockMvc mvc;

    @Before
    public void resetMachine() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/reset"));
    }

    @Test
    public void testBasicExchange() throws Exception {
        String exchangeAmount = "{\"numBills\": [1,1,1,0,1,0,0]}";
        String output = "{\"result\":\"" + StringConstants.successfulExchange + "\",\"pennies\":0,\"nickel\":0,\"dime\":30,\"quarter\":100}";
        ResultActions response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
        .andExpect(content().string(equalTo(output)));
    }

    @Test
    public void testExchangeTooMuch() throws Exception {
        String exchangeAmount = "{\"numBills\": [1,1,1,0,1,1,0]}";
        String output = "{\"result\":\"" + StringConstants.notEnoughCoins + "\",\"pennies\":0,\"nickel\":0,\"dime\":0,\"quarter\":0}";
        ResultActions response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
                .andExpect(content().string(equalTo(output)));
    }

    @Test
    public void testInvalidExchange() throws Exception {
        String exchangeAmount = "{\"numBills\": [1,1,1,0,-1,0,0]}";
        String output = "{\"result\":\"" + StringConstants.invalidBills + "\",\"pennies\":0,\"nickel\":0,\"dime\":0,\"quarter\":0}";
        ResultActions response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
                .andExpect(content().string(equalTo(output)));
    }

    @Test
    public void testRunOutOfCoinsAfterSuccess() throws Exception {
        String exchangeAmount = "{\"numBills\": [1,0,0,0,2,0,0]}";
        String output = "{\"result\":\"" + StringConstants.successfulExchange + " " + StringConstants.outOfCoins + "\",\"pennies\":100,\"nickel\":100,\"dime\":100,\"quarter\":100}";
        ResultActions response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
                .andExpect(content().string(equalTo(output)));
    }

    @Test
    public void testRunOutOfCoinsThenFail() throws Exception {
        String exchangeAmount = "{\"numBills\": [1,0,0,0,2,0,0]}";
        String output = "{\"result\":\"" + StringConstants.notEnoughCoins + " " + StringConstants.outOfCoins + "\",\"pennies\":0,\"nickel\":0,\"dime\":0,\"quarter\":0}";
        mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount));
        ResultActions response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
                .andExpect(content().string(equalTo(output)));
    }

    @Test
    public void TestReset() throws Exception {
        //the machine requires reset
        testRunOutOfCoinsAfterSuccess();

        //test that a otherwise valid exchange is failing due to there being no coins
        String exchangeAmount = "{\"numBills\": [1,1,1,0,1,0,0]}";
        String output = "{\"result\":\"" + StringConstants.notEnoughCoins + " " + StringConstants.outOfCoins + "\",\"pennies\":0,\"nickel\":0,\"dime\":0,\"quarter\":0}";
        ResultActions response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
                .andExpect(content().string(equalTo(output)));

        //reset the machine to have 100 of each coin
        mvc.perform(MockMvcRequestBuilders.post("/reset"));

        //test a basic valid exchange and make sure it is successful
        testBasicExchange();
    }

    @Test
    public void TestVariableReset() throws Exception {
        //the machine requires reset
        testRunOutOfCoinsAfterSuccess();

        //test that a otherwise valid exchange is failing due to there being no coins
        String exchangeAmount = "{\"numBills\": [1,1,1,0,1,0,0]}";
        String output = "{\"result\":\"" + StringConstants.notEnoughCoins + " " + StringConstants.outOfCoins + "\",\"pennies\":0,\"nickel\":0,\"dime\":0,\"quarter\":0}";
        ResultActions response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
                .andExpect(content().string(equalTo(output)));

        String numCoins = "[200,150,50,150]";
        //reset the machine to have 100 of each coin
        mvc.perform(MockMvcRequestBuilders.post("/reset").contentType(MediaType.APPLICATION_JSON).content(numCoins));

        //use up all the coins
        exchangeAmount = "{\"numBills\": [2,0,0,0,0,1,0]}";
        output = "{\"result\":\"" + StringConstants.successfulExchange + " " + StringConstants.outOfCoins + "\",\"pennies\":200,\"nickel\":150,\"dime\":50,\"quarter\":150}";
        response = mvc.perform(MockMvcRequestBuilders.post("/exchange").contentType(MediaType.APPLICATION_JSON).content(exchangeAmount))
                .andExpect(content().string(equalTo(output)));
    }

    public void testInvalidReset() throws Exception {
        String numCoins = "[200,-150,50,150]";
        //reset the machine to have -150 nickels
        mvc.perform(MockMvcRequestBuilders.post("/reset").contentType(MediaType.APPLICATION_JSON).content(numCoins))
                .andExpect(content().string(equalTo(StringConstants.failedReset)));
    }
}
