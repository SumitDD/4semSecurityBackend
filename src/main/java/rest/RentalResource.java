package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.CreateRentalDTO;
import dto.RentalDTO;
import entities.User;
import facades.RentalFacade;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.SetupTestUsers;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class RentalResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final ExecutorService ES = Executors.newCachedThreadPool();
    private static final RentalFacade FACADE = RentalFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static String cachedResponse;
    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

  
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    
    @Path("setUpUsers")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public void setUpUsers() {
        SetupTestUsers.setUpUsers();
    }
    
    @Path("makerental")
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String makeRental(String createRentalDTO) {
        CreateRentalDTO newRental = GSON.fromJson(createRentalDTO, CreateRentalDTO.class);
        RentalDTO createdRental = FACADE.createRental(newRental);
        return GSON.toJson(createdRental);
    }
    

    
    
    
}