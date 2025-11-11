package com.lab6.employee.Controller;

import com.lab6.employee.Api.ApiResponse;
import com.lab6.employee.Model.EmployeeSystem;
import jakarta.validation.Valid;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {
    ArrayList<EmployeeSystem> employees = new ArrayList<>();

    @GetMapping("/get-employees")
    public ResponseEntity<?> getEmployees(){
        if(employees.isEmpty()){
            return ResponseEntity.status(400).body(new ApiResponse("No employee records"));
        }
        return ResponseEntity.status(200).body(employees);
    }

    @GetMapping("/search-position/{position}")
    public ResponseEntity<?> searchByPosition(@PathVariable String position){
        ArrayList<EmployeeSystem> matchedEmployees = new ArrayList<>();
        // no need to check for it equalTo checked before entry
        for (int i = 0; i < employees.size(); i++){
            if(employees.get(i).getPosition().equalsIgnoreCase(position)){
                matchedEmployees.add(employees.get(i));
            }
        }
        if(matchedEmployees.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No employee records"));
        }
        return ResponseEntity.status(200).body(matchedEmployees);
    }

    @GetMapping("/search-age/{min}/{max}")
    public ResponseEntity<?> searchByAge(@PathVariable int min, @PathVariable int max){
        if(min < 0 || max < 0 || min > max || max > 120){
            return ResponseEntity.status(400).body(new ApiResponse("please enter valid age ranges"));
        }

        ArrayList<EmployeeSystem> matchedEmployees = new ArrayList<>();
        for (int i = 0; i < employees.size(); i++){
            if(employees.get(i).getAge() >= min && employees.get(i).getAge() <=max){
                matchedEmployees.add(employees.get(i));
            }
        }
        if(matchedEmployees.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No employee records"));
        }
        return ResponseEntity.status(200).body(matchedEmployees);
    }

    @GetMapping("/no-annual-leave")
    public ResponseEntity<?> searchNoAnnualLeave(){
        ArrayList<EmployeeSystem> matchedEmployees = new ArrayList<>();
        for (int i = 0; i < employees.size(); i++){
            if(employees.get(i).getAnnualLeave() == 0){
                matchedEmployees.add(employees.get(i));
            }
        }
        if(matchedEmployees.isEmpty()) {
            return ResponseEntity.status(400).body(new ApiResponse("No employee records"));
        }
        return ResponseEntity.status(200).body(matchedEmployees);
    }

    @PostMapping("/create-employee")
    public ResponseEntity<?> createEmployee(@RequestBody @Valid EmployeeSystem employee, Errors errors){
        if (errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));
        }
        employee.setPosition(employee.getPosition().toLowerCase());
        employees.add(employee);
        return ResponseEntity.status(200).body(new ApiResponse("employee record created successfully"));
    }

    @PutMapping("/update-employee/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable String id, @RequestBody @Valid EmployeeSystem employee, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));
        }

        for (int i = 0; i < employees.size(); i++){
            if(employees.get(i).getId().equalsIgnoreCase(id)){
                employees.set(i, employee);
                return ResponseEntity.status(200).body(
                        new ApiResponse(String.format("employee with id: %s was updated successfully", id)));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("No employee records found"));
    }

    @PutMapping("/promote-employee/{managerId}/{employeeId}")
    public ResponseEntity<?> promoteEmployee(@PathVariable String managerId, @PathVariable String employeeId){
        EmployeeSystem manager = null;
        EmployeeSystem employee = null;
        int index = -1;
        for (int i = 0; i < employees.size(); i++){
            if(employees.get(i).getId().equalsIgnoreCase(managerId)){
                if(employees.get(i).getPosition().equalsIgnoreCase("supervisor")){
                    manager = employees.get(i);
                }else return ResponseEntity.status(400).body(new ApiResponse("you don't have the authority to promote"));
            }
            if(employees.get(i).getId().equalsIgnoreCase(employeeId)){
                if(!employees.get(i).getPosition().equalsIgnoreCase("supervisor") &&
                        !employees.get(i).isOnLeave() && employees.get(i).getAge() >= 30){
                    employee = employees.get(i);
                    employee.setPosition("supervisor");
                    index = i;
                }else return ResponseEntity.status(400).body(new ApiResponse("employee doesn't meet the criteria "));
            }
        }
        if(manager == null || employee == null){
            return ResponseEntity.status(400).body(new ApiResponse("No employee records found"));
        }
        employees.set(index, employee);
        return ResponseEntity.status(200).body(new ApiResponse(
                String.format("employee with id: %s was promoted to supervisor", employeeId)));

    }

    @PutMapping("/apply-annual-leave/{id}")
    public ResponseEntity<?> applyForAnnualLeave(@PathVariable String id){
        for (int i = 0; i < employees.size(); i++){
            if(employees.get(i).getId().equalsIgnoreCase(id)){
                if(!employees.get(i).isOnLeave() && employees.get(i).getAnnualLeave() >= 1){
                    employees.get(i).setAnnualLeave(employees.get(i).getAnnualLeave() - 1);
                    employees.get(i).setOnLeave(true);
                    return ResponseEntity.status(200).body(
                            new ApiResponse(String.format("employee with id: %s was updated successfully", id)));
                }else return ResponseEntity.status(400).body(
                        new ApiResponse(String.format("employee with id: %s has no annual leaves left", id)));
                }

        }
        return ResponseEntity.status(400).body(new ApiResponse("No employee records found"));
    }

    @DeleteMapping("/delete-employee/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String id){
        for (int i = 0; i < employees.size(); i++){
            if(employees.get(i).getId().equalsIgnoreCase(id)){
               employees.remove(i);
                return ResponseEntity.status(200).body(
                        new ApiResponse(String.format("employee with id: %s was deleted successfully", id)));
            }
        }
        return ResponseEntity.status(400).body(new ApiResponse("No employee records found"));
    }


}
