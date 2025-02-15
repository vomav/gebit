
import ResponsivePopover from "sap/m/ResponsivePopover";
import Event from "sap/ui/base/Event";
import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";

/**
 * @namespace ui5.gebit.app.controller
 */
export default class App extends Controller {

    navigationPopover: ResponsivePopover;
    public onLogoPressed (oEvent:Event) {
        (this.getOwnerComponent() as UIComponent).getRouter().navTo("home");
    }

    public onSandwichButtonPress(oEvent: Event) {
        let that = this;
        if (this.navigationPopover == null) {
            this.loadFragment({ name: "ui5.gebit.app.fragment.NavigationPopover", addToDependents: true }).then(function (dialog: any) {
                that.navigationPopover = dialog as ResponsivePopover;
                that.navigationPopover.openBy(oEvent.getSource());
            }.bind(this));
        } else {
            that.navigationPopover.openBy(oEvent.getSource());
        }
    }

    public onNavItemPress(oEvent: Event) {
        let navTo = oEvent.getSource().data("nav");
        (this.getOwnerComponent() as UIComponent).getRouter().navTo(navTo);
    }
}
