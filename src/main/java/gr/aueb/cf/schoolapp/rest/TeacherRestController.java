package gr.aueb.cf.schoolapp.rest;

import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.dto.TeacherUpdateDTO;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.ITeacherService;
import gr.aueb.cf.schoolapp.service.exceptions.EntityNotFoundException;
import gr.aueb.cf.schoolapp.validator.ValidatorUtil;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Path("/teachers")
public class TeacherRestController {

    @Inject
    private ITeacherService teacherService;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherByLastname(@QueryParam("lastname") String lastname){
        List<Teacher> teachers;
        try{
            teachers = teacherService.getTeachersByLastname(lastname);
            List<TeacherReadOnlyDTO> teachersDto = new ArrayList<>();
            for(Teacher teacher : teachers){
                teachersDto.add(map(teacher));
            }
            return Response.status(Response.Status.OK).entity(teachersDto).build();
        }catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).entity("Not Found").build();
        }
    }

    @GET
    @Path("/{teacherId}")
    public Response getTeacher(@PathParam("teacherId") Long teacherId){
        Teacher teacher;
        try{
            teacher = teacherService.getTeacherById(teacherId);
            TeacherReadOnlyDTO teacherDto = new TeacherReadOnlyDTO();
            teacherDto = map(teacher);
            return Response.status(Response.Status.OK).entity(teacherDto).build();
        }catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).entity("Not Found").build();
        }
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTeacher(TeacherInsertDTO dto, UriInfo uriInfo){
        List<String> errors = ValidatorUtil.validateDTO(dto);
        Teacher teacher;
        if(!errors.isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();

        try{
            teacher = teacherService.insertTeacher(dto);
            TeacherReadOnlyDTO teacherDto = map(teacher);

            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
            URI uri = uriBuilder.path(Long.toString(teacherDto.getId())).build();
            return Response.status(Response.Status.OK).location(uri).entity(teacherDto).build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).entity("Teacher insert error").build();
        }
    }

    @PUT
    @Path("/{teacherId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTeacher(@PathParam("teacherId") Long teacherId, TeacherUpdateDTO dto){
        List<String> errors = ValidatorUtil.validateDTO(dto);
        Teacher teacher;
        if(!errors.isEmpty())
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();

        try{
            if(!Objects.equals(dto.getId(), teacherId)){
                return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
            }
            teacher = teacherService.updateTeacher(dto);
            TeacherReadOnlyDTO teacherDto = map(teacher);

            return Response.status(Response.Status.OK).entity(teacherDto).build();
        }catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).entity("Teacher not found").build();
        }
    }

    @DELETE
    @Path("/{teacherId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTeacher(@PathParam("teacherId") Long teacherId){
        Teacher teacher;
        try{
            teacher = teacherService.getTeacherById(teacherId);
            teacherService.deleteTeacher(teacherId);
            TeacherReadOnlyDTO teacherDto =  map(teacher);
            return Response.status(Response.Status.OK).entity(teacherDto).build();
        }catch (EntityNotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).entity("Not Found").build();
        }
    }

    private TeacherReadOnlyDTO map(Teacher teacher){
        TeacherReadOnlyDTO teacherDto = new TeacherReadOnlyDTO();
        teacherDto.setId(teacher.getId());
        teacherDto.setFirstname(teacherDto.getFirstname());
        teacherDto.setLastname(teacherDto.getLastname());
        return teacherDto;
    }
}
