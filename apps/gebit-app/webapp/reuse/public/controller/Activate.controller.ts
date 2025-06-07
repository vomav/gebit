import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import AppComponent from "../../../Component";
import UIComponent from "sap/ui/core/UIComponent";
import { URLHelper } from "sap/m/library";
import Event from "sap/ui/base/Event";
import Input from "sap/m/Input";

/**
 * @namespace ui5.gebit.app.reuse.public.controller
 */
export default class Activate extends Controller {

    tenantId: string | undefined;
    userId: string | undefined;

    public onInit() : void {
        let router = (this.getOwnerComponent() as UIComponent).getRouter();
        router.attachRouteMatched(this.attachRouteMatched, this);

    }

    public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
        let routeName = oEvent.getParameter("name");
        if(routeName == "activate") {
            this.tenantId = oEvent.getParameter("arguments").tenantId;
            this.userId = oEvent.getParameter("arguments").userId;
        }
    }
    public async activate(): Promise<void> {
        let model = (this.getOwnerComponent() as UIComponent).getModel() as ODataModel;
        let context = await model.bindContext("/activateAccount(...)");
        context.setParameter("userId", this.userId);
        context.setParameter("activationCode", this.getView()?.byId("activate_otp")?.getProperty("value"));

        context.execute().then(function () {
            MessageBox.success("Ok");
            this.getOwnerComponent().getRouter().navTo("login");
        }.bind(this), function (oError) {
            MessageBox.error(oError.message);
        }
        );
    }

}