package uk.ac.shu.webarch.register

import org.springframework.dao.DataIntegrityViolationException

class InstructorController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
    }


    def login() { 		
      
      def anInstructor = Instructor.findByInstructorNo(params.username)	//Read data from g:form on view. 
      
      if((anInstructor.instructorNo == params.username) && (anInstructor.password == params.password)){ 
        flash.message = "Login Successful"			//Verify login details.
        session.user = "${anInstructor.name}"			//Use flash method to display message on view.
        							//Begin new session.
							
      }else{							
        flash.message = "Login Failed"				//Else login unsuccessful.	
      }
    
     redirect(action: 'index')					//Do not search for view called Login.
           
    }
    //End of Login action.



    def logout = {
      session.user = null					//End session.
      redirect(action: 'index')			
    }
    //End of Logout action.




	//Scaffold CRUD operations.

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [instructorInstanceList: Instructor.list(params), instructorInstanceTotal: Instructor.count()]
    }

    def create() {
        [instructorInstance: new Instructor(params)]
    }

    def save() {
        def instructorInstance = new Instructor(params)
        if (!instructorInstance.save(flush: true)) {
            render(view: "create", model: [instructorInstance: instructorInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'instructor.label', default: 'Instructor'), instructorInstance.id])
        redirect(action: "show", id: instructorInstance.id)
    }

    def show(Long id) {
        def instructorInstance = Instructor.get(id)
        if (!instructorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'instructor.label', default: 'Instructor'), id])
            redirect(action: "list")
            return
        }

        [instructorInstance: instructorInstance]
    }

    def edit(Long id) {
        def instructorInstance = Instructor.get(id)
        if (!instructorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'instructor.label', default: 'Instructor'), id])
            redirect(action: "list")
            return
        }

        [instructorInstance: instructorInstance]
    }

    def update(Long id, Long version) {
        def instructorInstance = Instructor.get(id)
        if (!instructorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'instructor.label', default: 'Instructor'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (instructorInstance.version > version) {
                instructorInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'instructor.label', default: 'Instructor')] as Object[],
                          "Another user has updated this Instructor while you were editing")
                render(view: "edit", model: [instructorInstance: instructorInstance])
                return
            }
        }

        instructorInstance.properties = params

        if (!instructorInstance.save(flush: true)) {
            render(view: "edit", model: [instructorInstance: instructorInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'instructor.label', default: 'Instructor'), instructorInstance.id])
        redirect(action: "show", id: instructorInstance.id)
    }

    def delete(Long id) {
        def instructorInstance = Instructor.get(id)
        if (!instructorInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'instructor.label', default: 'Instructor'), id])
            redirect(action: "list")
            return
        }

        try {
            instructorInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'instructor.label', default: 'Instructor'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'instructor.label', default: 'Instructor'), id])
            redirect(action: "show", id: id)
        }
    }
}
