import MessageBox from "sap/m/MessageBox";
import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import { Input$InputEvent } from "sap/ui/webc/main/Input";
import { Input$LiveChangeEvent } from "sap/m/Input";
import Button from "sap/m/Button";

/**
 * @namespace ui5.gebit.app.reuse.public.controller
 */
export default class Activate extends Controller {

    tenantId: string | undefined;
    userId: string | undefined;
    code: string | undefined;

    public onInit() : void {
        let router = (this.getOwnerComponent() as UIComponent).getRouter();
        router.attachRouteMatched(this.attachRouteMatched, this);

    }

    public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
        let routeName = oEvent.getParameter("name");
        if(routeName == "activate") {
            this.tenantId = oEvent.getParameter("arguments")?.tenantId;
            this.userId = oEvent.getParameter("arguments")?.userId;
        }
    }
    public async activate(): Promise<void> {
        await this.wait(500);
        if(this.code === undefined ) {
            this.code = this.getView()?.byId("activate_otp")?.getProperty("value");
        }
        let model = (this.getOwnerComponent() as UIComponent).getModel() as ODataModel;
        let context = await model.bindContext("/activateAccount(...)");
        context.setParameter("userId", this.userId);
        context.setParameter("activationCode", this.code);

        context.execute().then(function () {
            MessageBox.success("Ok");
            this.getOwnerComponent().getRouter().navTo("login");
        }.bind(this), function (oError) {
            MessageBox.error(oError.message);
        }
        );
    }

    public onTypeActivationCode(oEvent:Input$LiveChangeEvent): void {
        let value = oEvent.getParameter("value");
        let activateButton = this.getView()?.byId("activateButton") as Button;
        if (activateButton && value !== undefined) {
            activateButton.setEnabled(value && value.length > 0);
            this.code =value;
        }
    }

   public async wait(ms: number): Promise<void> {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

}