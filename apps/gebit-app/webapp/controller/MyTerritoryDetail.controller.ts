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
import Context from "sap/ui/model/odata/v4/Context";
import { Dialog$ClosedEvent } from "sap/ui/commons/Dialog";
import MessageToast from "sap/m/MessageToast";
import ODataModel from "sap/ui/model/odata/v4/ODataModel";
import MessageBox from "sap/m/MessageBox";
import { Input$SuggestionItemSelectEvent } from "sap/ui/webc/main/Input";
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

	public onFileChange(oEvent: Event) {
		var file = oEvent.getParameters().files[0];
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


	public async onActionButtonPress(oEvent: Event) {
		let oButton = oEvent.getSource() as Button;
		oEvent.getSource().getParent().getDependents()[0].open();
	}

	public onCloseEditTerritoryPatchDialog(oEvent: Dialog$ClosedEvent) {
		oEvent.getSource().getParent().close()
	}

	public async assignPartToMe(oEvent: Event) {
		let model = (this.getView()?.getModel() as ODataModel);
		let seletctedContext = oEvent.getSource().getBindingContext() as Context;
		let inWorkByCountBefore = seletctedContext.getObject().inWorkBy.length;
		if(inWorkByCountBefore == 0) {
			await seletctedContext.requestRefresh();
		
			if(seletctedContext.getObject().inWorkBy.length > 0) {
				let assignedToUser = seletctedContext.getObject().inWorkBy[0];
				let i18nText = this.getView()?.getModel("i18n")?.getProperty("partIsAlreadyTakenDialogText");
				i18nText = i18nText.replace("{0}", assignedToUser.surname);
				i18nText = i18nText.replace("{1}", assignedToUser.username);
				MessageBox.confirm(i18nText, {
					title: "Confirm",
					onClose: async function (action) {
						if (action === MessageBox.Action.OK) {
							
								let context = await model.bindContext("srv.searching.assignPartToMe(...)", seletctedContext);
								context.execute().then(function () {
									MessageToast.show("{i18n>ok}");
									this.getView()?.getModel().refresh();
								}.bind(this), function (oError) {
									MessageBox.error(oError.message);
								}
								);
							
						} else {
							this.getView()?.getModel().refresh();
						}
					}.bind(this), 
					styleClass: "",
					actions: [ MessageBox.Action.OK, MessageBox.Action.CANCEL ],
					emphasizedAction: MessageBox.Action.OK,
					initialFocus:  MessageBox.Action.CANCEL
				});

				return;
			}

		}

		
			let context = await model.bindContext("srv.searching.assignPartToMe(...)", seletctedContext);
			context.execute().then(function () {
				MessageToast.show("OK");
				this.getView()?.getModel().refresh();
			}.bind(this), function (oError) {
				MessageBox.error(oError.message);
			}
			);
		
		
	}


	public async onAssignedUserChangeEvent(oEvent: Input$ChangeEvent) {
		let input = oEvent.getSource();
		let oBindingContext = input.getBindingContext() as Context;
		if(input.getSelectedItem()) {
			return;
		}
		
		let freestyleName = oEvent.getParameter("newValue");
		let model = (this.getView()?.getModel() as ODataModel);
		let context = await model.bindContext("srv.searching.assignToUnregistredUser(...)", oBindingContext);
		context.setParameter("name", freestyleName);
		context.execute().then(function () {
			MessageToast.show("OK");
			this.getView()?.getModel().refresh();
			input.setValue("");
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		}.bind(this)
		);
		

	}

	public async onSuggestedAssignedUserSelected(oEvent:Input$SuggestionItemSelectEvent) {
		let oModel = this.getView()?.getModel();
		let context = await oModel.bindContext("srv.searching.assignPartToUser(...)", oEvent.getSource().getBindingContext());
		context.setParameter("userId", oEvent.getParameter("selectedItem").getKey());

		context.execute().then(function () {
			MessageToast.show("OK");
			this.getView()?.getModel().refresh();
			oEvent.getSource().setValue("");
		}.bind(this), function (oError) {
			MessageBox.error(oError.message);
		});

	}

}