package com.luv2code.jobportal.controller;


import com.luv2code.jobportal.entity.*;
import com.luv2code.jobportal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class JobSeekerApplyController {

    private final JobPostedActivityService jobPostedActivityService;

    private final UsersService usersService;

    private final JobSeekerApplyService jobSeekerApplyService;

    private final JobSeekerSaveService jobSeekerSaveService;

    private final RecruiterProfileService recruiterProfileService;

    private final JobSeekerProfileService jobSeekerProfileService;



    @Autowired
    public JobSeekerApplyController(JobPostedActivityService jobPostedActivityService, UsersService usersService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService, RecruiterProfileService recruiterProfileService, JobSeekerProfileService jobSeekerProfileService) {
        this.jobPostedActivityService = jobPostedActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }



    @GetMapping("/job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model){

        JobPostActivity jobPostActivity=jobPostedActivityService.getOne(id);

        List<JobSeekerApply> jobSeekerApplyList=jobSeekerApplyService.getJobCandidates(jobPostActivity);
        List<JobSeekerSave> jobSeekerSaveList=jobSeekerSaveService.getJobCandidates(jobPostActivity);

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AbstractAuthenticationToken)){
            if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
                RecruiterProfile user=recruiterProfileService.getCurrentRecruiterProfile();
                if(user!=null){
                    model.addAttribute("applyList",jobSeekerApplyList);
                }

            }else {
                JobSeekerProfile user=jobSeekerProfileService.getCurrentSeekerProfile();
                if(user!=null){

                    boolean exists=false;
                    boolean saved=false;


                    for(JobSeekerApply jobSeekerApply:jobSeekerApplyList){
                        if(jobSeekerApply.getUserId().getUserAccountId()==user.getUserAccountId()){
                            exists=true;
                            break;


                        }

                    }
                    for(JobSeekerSave jobSeekerSave:jobSeekerSaveList){
                        if(jobSeekerSave.getUserId().getUserAccountId()==user.getUserAccountId()){
                            saved=true;
                            break;


                        }

                    }
                    model.addAttribute(("alreadyApplied"),exists);
                    model.addAttribute(("alreadySaved"),saved);





                }
            }
        }

        JobSeekerApply jobSeekerApply= new JobSeekerApply();
        model.addAttribute("applyJob",jobSeekerApply);

        model.addAttribute("jobDetails",jobPostActivity);
        model.addAttribute("user",usersService.getCurrentUserProfile());


        return "job-details";
    }

    @PostMapping("job-details/apply/{id}")
    public String apply(@PathVariable("id") int id,JobSeekerApply jobSeekerApply){

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AbstractAuthenticationToken)){
            String currentUsername=authentication.getName();

            Users user=usersService.findByEmail(currentUsername);
            Optional<JobSeekerProfile> seekerProfile=jobSeekerProfileService.getOne(user.getUserId());
            JobPostActivity jobPostActivity=jobPostedActivityService.getOne(id);

            if (seekerProfile.isPresent() && jobPostActivity != null) {
                jobSeekerApply = new JobSeekerApply();
                jobSeekerApply.setUserId(seekerProfile.get());
                jobSeekerApply.setJob(jobPostActivity);
                jobSeekerApply.setApplyDate(new Date());

            }else {
                throw new RuntimeException("User not found");
            }
            jobSeekerApplyService.addNew(jobSeekerApply);



        }

        return "redirect:/dashboard/";
















    }

}
