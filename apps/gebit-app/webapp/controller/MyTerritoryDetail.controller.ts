import Controller from "sap/ui/core/mvc/Controller";
import UIComponent from "sap/ui/core/UIComponent";
import { Router$RouteMatchedEvent } from "sap/ui/core/routing/Router";
import Event from "sap/ui/base/Event";
import Dialog from "sap/m/Dialog";
import Toolbar from "sap/m/Toolbar";
import ToolbarSpacer from "sap/m/ToolbarSpacer";
import Button from "sap/m/Button";
import Image from "sap/m/Image";
import Carousel from "sap/m/Carousel";
/**
 * @namespace ui5.gebit.app.controller
 */           
export default class MyTerritoryDetail extends Controller {
	viewMapSnapshotDialog: Dialog;
	public onInit() : void {
		let router = (this.getOwnerComponent() as UIComponent).getRouter();
		router.attachRouteMatched(this.attachRouteMatched, this);
	}

	public attachRouteMatched(oEvent:Router$RouteMatchedEvent) {
		let routeName = oEvent.getParameter("name");
		if(routeName == "myTerritoryDetail") {
			this.matched(oEvent.getParameter("arguments").id);
		}
	}

	public matched(context:string) {
		(this.getView() as any).bindElement("/TerritoryAssignments("+context+")");
	}

	public onActionButtonPress(oEvent:Event) {
		var oButton = oEvent.getSource();
		this.getView().byId("myPartsactionSheet").openBy(oButton);
	}


	public formatLinkToEmbedHtml(link:string) {
		let escapedLink = link.replace("&", "&amp;")
		// return "&lt;iframe src=&quot;" + escapedLink + "&quot width=&quot;100%&quot; height=&quot;480&quot;&gt;&lt;/iframe&gt;"
		return "<iframe src=\"" +link+ "\" width=\"100%\" height=\"480\"></iframe>"
	}

		public onFileChange(oEvent: Event) {
		var file = oEvent.getParameters("files").files[0];
		//Upload image
		var reader = new FileReader();
		reader.onload = function (oReaderEvent) {
			// get an access to the content of the file
			let content = oReaderEvent.currentTarget.result;
			this.createFile(file, content, oEvent.getSource().getBindingContext());
		}.bind(this);
		reader.readAsDataURL(file);
	}

	public async createFile(file:any, content:any, bindingContext: Context) {
		var odataModel = this.getOwnerComponent().getModel();
	
		let context = await odataModel.bindContext("srv.searching.uploadImage(...)", bindingContext);
		context.setParameter("file", content);
		context.setParameter("mediaType", file.type);

		context.execute().then(function () {
			MessageToast.show("{i18n>ok}");
			this.getView()?.getModel().refresh();
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}
		);
	}

	public onPressViewWorkedImage(oEvent:Event) {
		if(this.viewMapSnapshotDialog == undefined) {
			this.viewMapSnapshotDialog = new Dialog({
				showHeader: true,
				customHeader: new Toolbar({
					content:[
						new ToolbarSpacer(),
						new Button({
							icon: "sap-icon://decline",
							press: this.closeViewImageDialog.bind(this)
						})
					]
				}),
				content: [
					new Carousel({
						id:"myTerrmapSnapshotCarousel"
					})
				]
			});
		}

		let mapSnapshotCarousel = this.viewMapSnapshotDialog.getContent()[0] as Carousel;
		mapSnapshotCarousel.removeAllPages();
		let image = new Image({
			src:oEvent.getSource().data("imageUrl"),
			// width: window.screen.width - 30 + "px",
			// height: window.screen.height - 30 + "px"
		});
		mapSnapshotCarousel.addPage(image);
		// this.viewMapSnapshotDialog.removeAllContent();
		// this.viewMapSnapshotDialog.addContent(image);
		this.viewMapSnapshotDialog.open();
	}

		public closeViewImageDialog(oEvent: Event) {
			this.viewMapSnapshotDialog.close();
		}
}