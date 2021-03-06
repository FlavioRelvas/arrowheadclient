/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.ArrowheadConsumer.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PreferredProvider {

  private ArrowheadSystem providerSystem;
  private ArrowheadCloud providerCloud;

  public PreferredProvider() {
  }

  public PreferredProvider(ArrowheadSystem providerSystem, ArrowheadCloud providerCloud) {
    this.providerSystem = providerSystem;
    this.providerCloud = providerCloud;
  }

  public PreferredProvider(PreferredProviderSupport supportProvider) {
    this.providerSystem = new ArrowheadSystem(supportProvider.getProviderSystem());
    this.providerCloud = supportProvider.getProviderCloud();
  }

  public ArrowheadSystem getProviderSystem() {
    return providerSystem;
  }

  public void setProviderSystem(ArrowheadSystem providerSystem) {
    this.providerSystem = providerSystem;
  }

  public ArrowheadCloud getProviderCloud() {
    return providerCloud;
  }

  public void setProviderCloud(ArrowheadCloud providerCloud) {
    this.providerCloud = providerCloud;
  }

  public boolean isValid() {
    return isLocal() || isGlobal();
  }

  public boolean isLocal() {
    if (providerSystem != null) {
      providerSystem.missingFields(true, new HashSet<>(Collections.singleton("address")));
    }
    return providerCloud == null;
  }

  public boolean isGlobal() {
    if (providerCloud != null) {
      Set<String> mandatoryFields = new HashSet<>(Arrays.asList("address", "gatekeeperServiceURI"));
      providerCloud.missingFields(true, mandatoryFields);
    }
    return true;
  }

  @Override
  public String toString() {
    return "PreferredProvider{" + "providerSystem=" + providerSystem + ", providerCloud=" + providerCloud + '}';
  }

}
