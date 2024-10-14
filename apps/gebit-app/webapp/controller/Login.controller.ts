import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../Component";
import UIComponent from "sap/ui/core/UIComponent";

/**
 * @namespace ui5.gebit.app.controller
 */
export default class Login extends Controller {

	public onInit() : void {
	}

    public login(oEvent:any) {
        let login = this.getView()?.byId("login_loginInput")?.getProperty("value");
        let password = this.getView()?.byId("login_passwordInput")?.getProperty("value");
        let data = {
            "login":login,
            "password":password
        };
        
        let that = this;
        $.ajax({
            method:"POST",
            crossDomain: true,
            dataType: "json",
            contentType: "application/json",
            url: "http://localhost:8080/api/auth/login",
            data: JSON.stringify(data),
            // headers: {
            //     "Content-Type": "application/json",
            //     "Accept": "*/*"
            // },
            
            success: function(data:any) {
                localStorage.setItem('accessToken', data.accessToken);
                (that.getOwnerComponent() as UIComponent).getRouter().navTo("home");
            },
            error: function(err) {
                console.log(err.status);
                console.log(err.statusText);

            }
        });
    }

    public toToRegistration(oEvent:any) {
        (this.getOwnerComponent() as UIComponent).getRouter().navTo("registration");
    }
}